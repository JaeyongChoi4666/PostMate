#!/usr/bin/env bash
set -Eeuo pipefail

artifact=${1:?Usage: bash scripts/deploy.sh path/to/app.jar}
root=${DEPLOY_ROOT:-/opt/postmate}
port=${APP_PORT:-10001}
public_url=${PUBLIC_URL:-https://postmate.sunilab.work}
test "$root" = /opt/postmate
test -s "$artifact"
test -L "$root/current"

exec 9>"$root/.deploy.lock"
flock -n 9 || { printf 'Another PostMate deployment is running.\n' >&2; exit 1; }
previous=$(readlink -f "$root/current")
case "$previous" in "$root"/releases/*) ;; *) printf 'Unexpected current release path.\n' >&2; exit 1 ;; esac
test -s "$previous/app.jar"
revision=$(git rev-parse HEAD)
release="$root/releases/$(TZ=Asia/Seoul date +%Y%m%d_%H%M%S)_${BUILD_NUMBER:-manual}_${revision:0:7}"
test ! -e "$release"
mkdir "$release"
install -m 0644 "$artifact" "$release/app.jar"
printf '%s\n' "$revision" > "$release/REVISION"
printf '%s\n' "$previous" > "$release/previous-release"

switch_release() {
    local target=$1
    local temporary="$root/.current-$$"
    ln -s "$target" "$temporary"
    mv -Tf "$temporary" "$root/current"
}

switched=0
verified=0
on_exit() {
    local result=$?
    trap - EXIT
    if [ "$switched" = 1 ] && [ "$verified" != 1 ]; then
        printf 'Restoring previous release: %s\n' "$previous" >&2
        if ! switch_release "$previous" || ! sudo -n systemctl restart postmate; then
            printf 'ROLLBACK FAILED: manual intervention required.\n' >&2
            result=1
        fi
    fi
    exit "$result"
}
trap on_exit EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

switch_release "$release"
switched=1
sudo -n systemctl restart postmate
healthy=0
for attempt in $(seq 1 60); do
    if systemctl is-active --quiet postmate &&
       curl -fsS --max-time 5 "http://127.0.0.1:$port/" -o /dev/null 2>/dev/null &&
       curl -fsS --max-time 5 "http://127.0.0.1:$port/loadScheduleCategory" -o "$release/healthcheck.json" 2>/dev/null &&
       python3 -c 'import json,sys; data=json.load(open(sys.argv[1])); assert isinstance(data,list) and data and all("CATE_NO" in row for row in data)' "$release/healthcheck.json"; then
        healthy=1
        break
    fi
    sleep 2
done
test "$healthy" = 1
curl -fsS --retry 3 --retry-delay 2 --max-time 15 "$public_url/" -o /dev/null
verified=1
printf 'Deployment verified: %s (%s)\n' "$release" "$revision"
