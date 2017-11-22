package com.example.deekshithamanjunath.assignment5;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
/**
 * Created by deekshithamanjunath on 4/2/17.
 */

public class VolleySingleton {
    private static VolleySingleton instance=null;
    private RequestQueue vsRequestQ;


    private VolleySingleton()
    {
        vsRequestQ = Volley.newRequestQueue(ApplicationActivity.getContext());
    }

    public static VolleySingleton getInstance()
    {
        if (instance==null)
        {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public RequestQueue getVSRequestQueue()
    {
        return vsRequestQ;
    }
}
