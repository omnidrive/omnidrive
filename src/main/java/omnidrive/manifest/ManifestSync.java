package omnidrive.manifest;

import omnidrive.api.base.CloudAccount;

import java.util.List;

public interface ManifestSync {

    void uploadToAll(List<CloudAccount> accounts) throws Exception;

    void uploadToAccount(CloudAccount account) throws Exception;

    Manifest downloadFromAccount(CloudAccount account) throws Exception;

}
