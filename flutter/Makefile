FLUTTER := flutter

.PHONY: pub-get run-stage run-dev run-pro run-web-stage run-web-dev run-web-pro build-android-stage build-android-dev build-android-pro build-web-stage build-web-dev build-web-pro analyze test flavorize

pub-get:
	$(FLUTTER) pub get

flavorize:
	dart run flutter_flavorizr -f

run-stage:
	$(FLUTTER) run --flavor stage --target lib/main_stage.dart --dart-define-from-file=flavors/stage.json

run-dev:
	$(FLUTTER) run --flavor dev --target lib/main_dev.dart --dart-define-from-file=flavors/dev.json

run-pro:
	$(FLUTTER) run --flavor pro --target lib/main_pro.dart --dart-define-from-file=flavors/pro.json

run-staging: run-stage

run-prod: run-pro

run-stage-web: run-web-stage

run-web-stage:
	$(FLUTTER) run -d chrome --target lib/main_stage.dart --dart-define-from-file=flavors/stage.json

run-web-dev:
	$(FLUTTER) run -d chrome --target lib/main_dev.dart --dart-define-from-file=flavors/dev.json

run-web-pro:
	$(FLUTTER) run -d chrome --target lib/main_pro.dart --dart-define-from-file=flavors/pro.json

run-web-staging: run-web-stage

run-web-prod: run-web-pro

build-android-stage:
	$(FLUTTER) build apk --flavor stage --target lib/main_stage.dart --dart-define-from-file=flavors/stage.json

build-android-dev:
	$(FLUTTER) build apk --flavor dev --target lib/main_dev.dart --dart-define-from-file=flavors/dev.json

build-android-pro:
	$(FLUTTER) build apk --flavor pro --target lib/main_pro.dart --dart-define-from-file=flavors/pro.json

build-android-staging: build-android-stage

build-android-prod: build-android-pro

build-web-stage:
	$(FLUTTER) build web --target lib/main_stage.dart --dart-define-from-file=flavors/stage.json

build-web-dev:
	$(FLUTTER) build web --target lib/main_dev.dart --dart-define-from-file=flavors/dev.json

build-web-pro:
	$(FLUTTER) build web --target lib/main_pro.dart --dart-define-from-file=flavors/pro.json

build-web-staging: build-web-stage

build-web-prod: build-web-pro

analyze:
	$(FLUTTER) analyze

test:
	$(FLUTTER) test