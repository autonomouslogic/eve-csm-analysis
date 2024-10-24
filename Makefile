.PHONY: analysis test lint format clean

analysis:
	./gradlew run --stacktrace

test:
	./gradlew test --stacktrace

lint:
	./gradlew spotlessCheck --stacktrace

format:
	./gradlew spotlessApply --stacktrace

clean:
	./gradlew clean --stacktrace
