package com.filelug.android.ui.widget;

/**
 * Created by Vincent Chang on 2016/4/25.
 * Copyright (c) 2016 Filelug. All rights reserved.
 */
public interface FileLayout {

    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_ONE_FOLDER = 1;
    public static final int CHOICE_MODE_MULTIPLE_FILES = 2;
    public static final int CHOICE_MODE_SINGLE_FILE = 3;

    int getChoiceMode();
    void setChoiceMode(int choiceMode);

}
