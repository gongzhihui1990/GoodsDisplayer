/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by heisenberg on 2017/7/21.
 * heisenberg.gong@koolpos.com
 */

public interface ApplicationObserverHolder<DialogParams> extends ObserverHolder {
    FragmentManager getSupportFragmentManager();

    Context getContext();

    void showDialog(DialogParams params);
}
