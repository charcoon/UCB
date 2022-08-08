import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Your Name
 */
class AlternatingFilter<Value> extends Filter<Value> {
    
    AlternatingFilter(Iterator<Value> input) {
        super(input);
        this._check = false;
    }

    @Override
    protected boolean keep() {
        _check = !(_check);
        return _check;  
    }

    private boolean _check;
}
