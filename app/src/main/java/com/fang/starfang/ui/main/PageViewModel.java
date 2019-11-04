package com.fang.starfang.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, input -> {
        String str;
        switch(input) {
            case 1:
                str = "search";
                break;
            case 2:
                str = "conversation";
                break;
            case 3:
                str = "reply";
                break;
                default:
                    str = "error";
        }
        return str;
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}