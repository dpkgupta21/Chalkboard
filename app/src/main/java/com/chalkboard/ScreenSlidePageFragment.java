package com.chalkboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScreenSlidePageFragment extends Fragment {

	int[] images = {R.drawable.background_1,R.drawable.background_2,R.drawable.background_3};
	
	public ScreenSlidePageFragment() {
		
	}
	
	public static ScreenSlidePageFragment newInstance(int image){
		
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		
		Bundle args = new Bundle();
		
		args.putInt("image", image);
		
		fragment.setArguments(args);
		
		
		return fragment;
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        int image = getArguments().getInt("image");
        
        ((ImageView) rootView.findViewById(R.id.content)).setBackgroundResource(images[image]);
        
        
        return rootView;
    }
}
