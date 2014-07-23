package com.hybridplay.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScreenSlidePageFragment extends Fragment {
	
    /**
     * Key to insert the background color into the mapping of a Bundle.
     */
    private static final String BACKGROUND_COLOR = "color";
 
    /**
     * Key to insert the index page into the mapping of a Bundle.
     */
    private static final String INDEX = "index";
    
    /**
     * Key to insert the body text into the mapping of a Bundle.
     */
    private static final String STRINGTXT = "stringTxt";
    
    private static final String IMAGEN = "imagen";
    
    private int color;
    public int index;
    public String stringTxt;
    private int img;
 
    /**
     * Instances a new fragment with a background color and an index page.
     * 
     * @param color
     *            background color
     * @param index
     *            index page
     * @return a new page
     */
    public static ScreenSlidePageFragment newInstance(int color, int index, String stringTxt, int img) {
 
        // Instantiate a new fragment
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
 
        // Save the parameters
        Bundle bundle = new Bundle();
        bundle.putInt(BACKGROUND_COLOR, color);
        bundle.putInt(INDEX, index);
        bundle.putString(STRINGTXT, stringTxt);
        bundle.putInt(IMAGEN, img);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
 
        return fragment;
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
 
        // Load parameters when the initial creation of the fragment is done
        this.color = (getArguments() != null) ? getArguments().getInt(
                BACKGROUND_COLOR) : Color.GRAY;
        this.index = (getArguments() != null) ? getArguments().getInt(INDEX)
                : -1;
        this.stringTxt = (getArguments() != null) ? getArguments().getString(STRINGTXT) : "Here should be a text";
        this.img =  (getArguments() != null) ? getArguments().getInt(IMAGEN)
                : -1;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
 
 
        // Change the background color
        rootView.setBackgroundColor(this.color);
        
        //show the image
        ImageView myImageView = (ImageView) rootView.findViewById(R.id.imageView);
        myImageView.setImageResource(this.img);
 
        return rootView;
    }
 
}
