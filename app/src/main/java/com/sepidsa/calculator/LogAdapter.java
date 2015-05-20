package com.sepidsa.calculator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sepidsa.calculator.data.LogContract;

/**
 * Created by Farshid on 5/17/2015.
 */
public class LogAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 1;
    private Context mContext;
    private String m_Text;


    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_group_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT));
        String operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION));
        String tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
        Boolean starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0;

        viewHolder.position = cursor.getInt((cursor.getColumnIndex(LogContract.LogEntry._ID)));

        viewHolder.resultView.setText(result);
        viewHolder.operationView.setText(operation);
        viewHolder.tagView.setText(tag);
        viewHolder.starredButton.setChecked(starred);
        viewHolder.starredButton.setOnClickListener(mStarOnClickListener);


        viewHolder.deleteButton.setOnClickListener(mDeleteButtonOnClickListener);
        viewHolder.copyButton.setOnClickListener(mLabelButtonOnClickListener);
        viewHolder.shareButton.setOnClickListener(mShareButtonOnClickListener);
        viewHolder.useButton.setOnClickListener(mUseButtonOnClickListener);


        if((viewHolder.toolbar.getVisibility() == View.VISIBLE) && !viewHolder.checked) {
            LinearLayout.LayoutParams viewLayoutParams = (LinearLayout.LayoutParams) viewHolder.toolbar.getLayoutParams();
            viewLayoutParams.bottomMargin = 0 - viewHolder.toolbar.getHeight();
            viewHolder.toolbar.setVisibility(View.GONE);
        } else {
            viewHolder.toolbar.setVisibility(View.VISIBLE);
            viewHolder.checked=false;
        }

    }

    public static class ViewHolder {
        public final TextView resultView;
        public final TextView operationView;
        public final TextView tagView;
        public final CheckBox starredButton;
        public final View toolbar;

        public int position;
        public boolean checked;

        public final Button deleteButton;
        public final Button copyButton;
        public final Button shareButton;
        public final Button useButton;

        public ViewHolder(View view) {

            resultView = (TextView) view.findViewById(R.id.Log_title);
            operationView = (TextView) view.findViewById(R.id.LOG_detail);
            tagView = (TextView) view.findViewById(R.id.LOG_tag);
            starredButton = (CheckBox) view.findViewById(R.id.add_to_favorites);
            toolbar = view.findViewById(R.id.toolbar);

            checked = false;

            deleteButton = (Button) view.findViewById(R.id.deleteButton);
            copyButton = (Button) view.findViewById(R.id.labelButton);
            shareButton = (Button) view.findViewById(R.id.shareButton);
            useButton = (Button) view.findViewById(R.id.useButton);


        }
    }


    private CompoundButton.OnClickListener mStarOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                viewHolder.checked = true;

                String selection = LogContract.LogEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = LogContract.LogEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    int isCheckedInteger = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED));
                    isCheckedInteger = 1 - isCheckedInteger;
                    ContentValues values = new ContentValues();
                    values.put(LogContract.LogEntry.COLUMN_STARRED, isCheckedInteger);
                    mContext.getContentResolver().update(
                            uri,
                            values,
                            selection,
                            selectionArgs
                    );

                    showMessage(Integer.toString(position) + isCheckedInteger);


                }
            }
        }
    };

    Button.OnClickListener mDeleteButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                showMessage("delete " + position);
                String selection = LogContract.LogEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = LogContract.LogEntry.CONTENT_URI;

                mContext.getContentResolver().delete(
                        uri,
                        selection,
                        selectionArgs
                );
            }
        }
    };


    Button.OnClickListener mLabelButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
//                viewHolder.checked = true;

                final String selection = LogContract.LogEntry._ID + "=?";
                final String[] selectionArgs = new String[]{String.valueOf(position)};
                final Uri uri = LogContract.LogEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    String currentLabel = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Enter a Label:");

                    // Set up the input

                    final EditText input = new EditText(mContext);
                   final InputMethodManager inputMethodManager = (InputMethodManager)  mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    input.setText(currentLabel);
                    input.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    // Set up the buttons

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newLabel = input.getText().toString();
                            ContentValues values = new ContentValues();
                            values.put(LogContract.LogEntry.COLUMN_TAG, newLabel);
                            mContext.getContentResolver().update(
                                    uri,
                                    values,
                                    selection,
                                    selectionArgs
                            );
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog.cancel();

                        }
                    });

                    builder.show();

                }
            }
        }
    };


    Button.OnClickListener mShareButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                String shareBody = viewHolder.operationView.getText() + " = " + viewHolder.resultView.getText();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String subject = viewHolder.tagView.getText().toString();

                if( !subject.equals("") ) {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, subject + ": " + shareBody);
                } else {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                }

                mContext.startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        }
    };


    Button.OnClickListener mUseButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                showMessage("use " + position);
            }
        }
    };


    public View findParentRecursively(View view) {
        if (view.getTag() != null) {
            return view;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findParentRecursively(parent);
    }

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


//    public View findParentById(View view, int targetId) {
//        if (view.getId() == targetId) {
//            return view;
//        }
//        View parent = (View) view.getParent();
//        if (parent == null) {
//            return null;
//        }
//        return findParentById(parent, targetId);
//    }

}