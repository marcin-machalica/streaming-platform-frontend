# Streaming platform

In order to make the application work, it is required to provide a path to the directory to store the videos in. The value responsible for the path is kept in the application.properties and is named **VIDEOS_STORAGE_PATH**, which defaults to */var/streaming_platform/videos_storage*.
User has to have permissions to read from and write to provided directory path.

To have access to keycloak admin api, there needs to be a user added to the **admin-api** keycloak group. This user's credentials should be mapped to **keycloak-api-username**, **keycloak-api-password** and **keycloak-admin-group-id** inside the application.properties file.
