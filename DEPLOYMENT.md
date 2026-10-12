# Automatic deployment

The Jenkins job `postMateAutoDeploy` reads the repository's `Jenkinsfile` from
`master`. It checks GitHub for changes every two minutes. Pushes to other branches
do not deploy. No build runs when the checked commit has not changed.

Jenkins is currently available on the private network at
`http://192.168.0.20:9091/`. GitHub cannot directly reach this address, so SCM
polling is used without exposing Jenkins publicly. The first build registers
the polling schedule; later pushes need no manual build action.

The pipeline runs all tests and builds the application before deployment.
It records JUnit results and retains ten builds and their successful JARs.
Concurrent builds are queued to prevent overlapping deployments.

Deployment runs as `jenkins` using `scripts/deploy.sh`. A new release is installed
under `/opt/postmate/releases`, then `/opt/postmate/current` is switched atomically.
The `postmate` systemd service is restarted. Jenkins verifies the local page,
database-backed category API, and public site at `https://postmate.sunilab.work/`.
An unsuccessful switch or health check restores the previous release and restarts
the service. A test/build failure leaves the running release unchanged.

Each release stores its full Git commit in `REVISION` and its rollback target in
`previous-release`. Releases are preserved; this pipeline does not delete them.
The `jenkins` service account must retain its existing passwordless permission
to restart `postmate`. The CI/CD configuration does not embed Jenkins or GitHub credentials.

To roll back manually on the server, obtain the target from the active release's
`previous-release`, confirm it is a directory below `/opt/postmate/releases` with
an `app.jar`, update `current` to that directory, and restart `postmate`.
Check the page and database API after rollback.
