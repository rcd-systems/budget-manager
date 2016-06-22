package systems.rcd.bm.model.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Type
{
    private final String name;

    private final Map<String, Type> subTypes = new HashMap<>();

    private Type parent;

    public Type( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Collection<Type> getSubTypes()
    {
        return subTypes.values();
    }

    public Type getParent()
    {
        return parent;
    }

    public Type addSubType( final Type subType )
    {
        if ( subType.parent != null )
        {
            subType.parent.subTypes.remove( subType );
        }
        subType.parent = this;
        subTypes.put( subType.name, subType );
        return this;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj instanceof Type )
        {
            return name.equals( ( (Type) obj ).name );
        }
        return false;
    }

    public boolean isDirectChildOf( final String type )
    {
        if ( this.parent == null )
        {
            return type == null;
        }
        return this.parent.getName().equals( type );
    }

    public boolean isChildOf( final String type )
    {
        if ( type == null )
        {
            return true;
        }
        if ( this.parent == null )
        {
            return false;
        }
        if ( this.parent.getName().equals( type ) )
        {
            return true;
        }
        return this.parent.isChildOf( type );
    }

    public boolean isOrChildOf( final String type )
    {
        if ( getName().equals( type ) )
        {
            return true;
        }
        return isChildOf( type );
    }

    @Override
    public String toString()
    {
        return name;
    }
}
