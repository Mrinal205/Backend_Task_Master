

#### Using non published maven libraries with Maven + Heroku ####


https://devcenter.heroku.com/articles/local-maven-dependencies

--TODO include sources

cp -r ~/.m2/repository/com/moonassist/* /tmp/

mvn deploy:deploy-file \
-Durl=file:///Users/ericanderson/Development/moonassist/backend-task/maven-repo/ \
-Dfile=/tmp/com/moonassist/backend-system-bind/0.0.2-SNAPSHOT/backend-system-bind-0.0.2-SNAPSHOT.jar \
-DpomFile=/tmp/com/moonassist/backend-system-bind/0.0.2-SNAPSHOT/backend-system-bind-0.0.2-SNAPSHOT.pom \
-DgroupId=com.moonassist \
-DartifactId=backend-system-bind -Dpackaging=jar \
-DrepositoryId=project.local

mvn deploy:deploy-file \
-Durl=file:///Users/ericanderson/Development/moonassist/backend-task/maven-repo/ \
-Dfile=/tmp/com/moonassist/backend-system-model/0.0.2-SNAPSHOT/backend-system-model-0.0.2-SNAPSHOT.jar \
-DpomFile=/tmp/com/moonassist/backend-system-model/0.0.2-SNAPSHOT/backend-system-model-0.0.2-SNAPSHOT.pom \
-DgroupId=com.moonassist \
-DartifactId=backend-system-model -Dpackaging=jar \
-DrepositoryId=project.local

mvn deploy:deploy-file \
-Durl=file:///Users/ericanderson/Development/moonassist/backend-task/maven-repo/ \
-Dfile=/tmp/com/moonassist/backend-system-service/0.0.2-SNAPSHOT/backend-system-service-0.0.2-SNAPSHOT.jar \
-DpomFile=/tmp/com/moonassist/backend-system-service/0.0.2-SNAPSHOT/backend-system-service-0.0.2-SNAPSHOT.pom \
-DgroupId=com.moonassist \
-DartifactId=backend-system-service -Dpackaging=jar \
-DrepositoryId=project.local







mvn deploy:deploy-file \
-Durl=file:///Users/ericanderson/Development/moonassist/backend-task/maven-repo/ \
-Dfile=/Users/ericanderson/Development/moonassist/backend-system/service/target/backend-system-service-0.0.2-SNAPSHOT.jar \
-DpomFile=/Users/ericanderson/Development/moonassist/backend-system/service/pom.xml \
-DgroupId=com.moonassist \
-DartifactId=backend-system-service -Dpackaging=jar \
-DrepositoryId=project.local \
-Dversion=0.0.2-SNAPSHOT