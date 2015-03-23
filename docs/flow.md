## FilesystemWatcher

Events:

* Create
    1. Choose provider
    2. Store file
        * Success
        * Error
    3. Update repository (up to the root)
        * Success
        * Error
* Delete
    1. Get file info from repository
    2. Update repository (up to the root)
        * Success
        * Error
    3. Delete from provider
        * Success
        * Error
* Modify

## RemoteRepositoryWatcher

Events:

* Change