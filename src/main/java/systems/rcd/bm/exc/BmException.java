package systems.rcd.bm.exc;

public class BmException
    extends Exception
{

    public BmException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public BmException( final String message )
    {
        super( message );
    }
}
