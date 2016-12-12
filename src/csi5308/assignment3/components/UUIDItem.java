package csi5308.assignment3.components;

public abstract class UUIDItem {

    private final int uuid;
    private volatile static int uuidGenerator = 0;

    public UUIDItem() {
        this.uuid = getUUID();
    }

    public int getUniqueID() {
        return this.uuid;
    }

    private synchronized int getUUID() {
        return uuidGenerator++;
    }

    @Override
    public int hashCode() {
        return this.uuid;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UUIDItem && uuid == ((UUIDItem) other).getUniqueID();
    }

}
