package com.els.button.Database;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Cam on 1/17/17.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeyConstraintsEnforced = true, insertConflict = ConflictAction.IGNORE, updateConflict= ConflictAction.REPLACE)
public class AppDatabase {

    public static final String NAME = "AppDatabase"; // we will add the .db extension

    public static final int VERSION = 8;
}