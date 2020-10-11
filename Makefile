build:
	./mvnw -s settings.xml spring-javaformat:apply package

deploy:
	cdk deploy
