# empik2025


Make sure you have docker and docker-compose installed [docker installation guide](https://docs.docker.com/compose/install/)
## Step 1: Configuration Setup
create ```.env``` file in root folder.
```
DB_ROOT_PASSWORD=mySecretRootPass
DB_DATABASE_NAME=world
DB_USER=dbuser
DB_PASSWORD=dbpasswword
JWT_ISSUER=https://github.com/Demongo2009
SWAGGER_CONTACT_MAIL=youremail
SWAGGER_CONTACT_URL=yourwebsite
SWAGGER_APP_NAME=Spring Boot Coupon Project
SWAGGER_APP_VERSION=2.0.0
SWAGGER_APP_LICENSE_URL=https://www.apache.org/licenses/LICENSE-2.0.html
SWAGGER_APP_LICENSE=Apache 2.0
```
This .env file contains the essential environment variables needed for your application to run.

## Step 2: Build Docker Images
Open a terminal or command prompt, navigate to your project's root folder, and run the following command to build the Docker images:
```
docker-compose build
```
This command will create Docker images based on the configurations defined in your docker-compose.yml file.
## Step 3: Start Application
After the Docker images are built, run the following command to start your application:
```
docker-compose up
```
Now, your application will be up and running. You can access it in your web browser at http://localhost:8000.

