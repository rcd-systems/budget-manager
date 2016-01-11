package systems.rcd.bm.model.data;

import java.util.Collection;
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

    public Collection<Type> getSubTypes() {
        return subTypes.values();
    }

    public Type getParent() {
        return parent;
    }

    public Type addSubType(final Type subType) {
        if (subType.parent != null) {
            subType.parent.subTypes.remove(subType);
        }
        subType.parent = this;
        subTypes.put(subType.name, subType);
        return this;
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

    public boolean isChildOf(final Type type) {
        if (this.parent == null) {
            return type == null;
        }
        if (type == null) {
            return false;
        }
        if (this.parent.equals(type)) {
            return true;
        }
        return this.parent.isChildOf(type);
    }

    public boolean isOrChildOf(final Type type) {
        if (equals(type)) {
            return true;
        }
        return isChildOf(type);
    }
}
