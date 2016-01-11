package systems.rcd.bm.model.data;

import java.util.HashMap;
import java.util.Map;

public class Type {
    private final String name;
    private final Map<String, Type> subTypes = new HashMap<>();
    private Type parent;

    public Type(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Type addSubType(final Type subType) {
        if (subType.parent != null) {
            subType.parent.subTypes.remove(subType);
        }
        subType.parent = this;

        final Type existingSubType = subTypes.put(subType.name, subType);
        if (existingSubType != null) {
            existingSubType.parent = null;
        }
        return this;
    }

    public Type getParent() {
        return parent;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Type) {
            return name.equals(((Type) obj).name);
        }
        return false;
    }

    public boolean isOrChildOf(final Type type) {
        if (type == null) {
            return false;
        }
        if (equals(type)) {
            return true;
        }
        if (parent != null) {
            return parent.isOrChildOf(type);
        }
        return false;
    }

    public boolean isChildOf(final Type parent) {
        if (this.parent == null) {
            return parent == null;
        }
        if (parent == null) {
            return false;
        }
        if (this.parent.equals(parent)) {
            return true;
        }
        return this.parent.isChildOf(parent);
    }

    public boolean isParentOf(final Type child) {
        for (final Type subtype : subTypes.values()) {
            if (subtype.equals(child)) {
                return true;
            }
        }

        for (final Type subtype : subTypes.values()) {
            if (subtype.isParentOf(child)) {
                return true;
            }
        }
        return false;
    }

}
