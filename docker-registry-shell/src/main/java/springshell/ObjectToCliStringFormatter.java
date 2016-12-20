package springshell;

import dockerregistry.model.Blob;
import dockerregistry.model.Image;
import java.util.List;

public class ObjectToCliStringFormatter {

    public String imageToString(Image image){
        String returnString = "";
        
        returnString = returnString 
                + stringPadLeft("size", 12) + " | "
                + stringPadRight("digest", 72) + " | "
                + stringPadRight("blob role", 10) + "\n";
        returnString = returnString + "--------------------------------------------------------------------------------------------------\n";
        
        //int manifestBlobSize = image.getManifest().getManifestBlob().getSize();
        String manifestHash = image.getManifest().getManifestBlob().getHash();
        returnString = returnString + stringPadLeft("--", 12) + " | "
                + stringPadRight(manifestHash, 72) + " | " 
                + stringPadRight("manifest",10) + "\n";             
        
        int configBlobSize = image.getManifest().getConfigBlob().getSize();
        String configHash = image.getManifest().getConfigBlob().getHash();
        returnString = returnString + stringPadLeft(Integer.toString(configBlobSize), 12) + " | "
                + stringPadRight(configHash, 72) + " | " 
                + stringPadRight("config",10) + "\n";
        
        List<Blob> layerBlobs = image.getManifest().getLayerBlobs();
        for (Blob layerBlob: layerBlobs){
            int layerSize = layerBlob.getSize();
            String layerHash = layerBlob.getHash();
            returnString = returnString + stringPadLeft(Integer.toString(layerSize), 12) + " | "
                    + stringPadRight(layerHash, 72) + " | " 
                    + stringPadRight("layer",10) + "\n";
        }                
        
        return returnString;
    }

    private String stringPadLeft(String string, int pad){
        return String.format("%1$" + pad + "s", string);
    }
    
    private String stringPadRight(String string, int pad){
        return String.format("%1$-" + pad + "s", string);
    }
    
}
