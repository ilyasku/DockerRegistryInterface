package dockerregistry.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to build a dependecy map of which images depend on which blobs.
 */
public class BlobImageDependencyChecker {

    private Map<String, List<String>> dependencyMap;
    
    void buildDependencyMap(Map<String, Image> imageMap) {
        dependencyMap = new HashMap<>();
        for (Map.Entry<String, Image> entry: imageMap.entrySet()){
            updateDependencyMap(entry.getKey(), entry.getValue().getManifest());
        }
    }

    private void updateDependencyMap(String imageName, Manifest manifestOfImage) {
        updateDependencyMap_BySingleBlob(imageName, manifestOfImage.getManifestBlob());
        updateDependencyMap_BySingleBlob(imageName, manifestOfImage.getConfigBlob());
        for (Blob layerBlob: manifestOfImage.getLayerBlobs()){
            updateDependencyMap_BySingleBlob(imageName, layerBlob);
        }
    }

    private void updateDependencyMap_BySingleBlob(String imageName, Blob blob) {
        String blobHash = blob.getHash();
        if (!dependencyMap.containsKey(blobHash)){
            List<String> newListOfImages = new ArrayList<>();
            newListOfImages.add(imageName);
            getDependencyMap().put(blobHash, newListOfImages);
        }
        else{
            List<String> listOfImagesReferencingThisBlob = getDependencyMap().get(blobHash);
            if (!listOfImagesReferencingThisBlob.contains(imageName)){
                listOfImagesReferencingThisBlob.add(imageName);
            }
        }
    }        

    public Map<String, List<String>> getDependencyMap() {
        return dependencyMap;
    }

    Map<String, List<String>> getDependencyMapWithImagesDeleted(String[] namesOfImagesToBeDeleted) {
        Map<String, List<String>> copyOfDependencyMapFromWhichImagesWillBeDeleted = new HashMap<>();
        for (String blobHash: dependencyMap.keySet()){
            List<String> listOfImagesReferencingThatBlobHash = new ArrayList<>(dependencyMap.get(blobHash));
            for (String imageToDelete: namesOfImagesToBeDeleted){
                if (listOfImagesReferencingThatBlobHash.contains(imageToDelete)){
                    listOfImagesReferencingThatBlobHash.remove(imageToDelete);
                }
            }
            copyOfDependencyMapFromWhichImagesWillBeDeleted.put(blobHash, listOfImagesReferencingThatBlobHash);
        }
        return copyOfDependencyMapFromWhichImagesWillBeDeleted;
    }    
}
