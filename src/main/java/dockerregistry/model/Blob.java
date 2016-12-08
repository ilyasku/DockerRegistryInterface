package dockerregistry.model;

import java.util.Objects;

public class Blob {    
    private final String hash;
    private final int size;
    
    public Blob(String hash, int size) {
        this.hash = hash;
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public int getSize() {
        return size;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Blob other = (Blob) obj;
        if (!Objects.equals(this.hash, other.hash)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.hash);
        return hash;
    }
}
