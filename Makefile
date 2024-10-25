.PHONY: analysis test lint format clean

process:
	./gradlew csmProcess --stacktrace

thymeleaf:
	./gradlew csmThymelead --stacktrace

test:
	./gradlew test --stacktrace

lint:
	./gradlew spotlessCheck --stacktrace

format:
	./gradlew spotlessApply --stacktrace

clean:
	./gradlew clean --stacktrace
