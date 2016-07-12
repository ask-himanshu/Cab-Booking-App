package driver.prototype.aptlegion.limocartdriver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Elitebook on 09-Jul-15.
 */
public class ridenow extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dummy, container, false);
        return view;
    }
}
