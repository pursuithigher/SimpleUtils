package com.dzbook.activity.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.type.BookNoteEvent;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.r.util.HwUtils;
import com.dzbook.service.SyncBookMarkService;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.HashMap;

/**
 * 阅读器笔记
 *
 * @author wxliao on 17/12/12.
 */
public class ReaderNoteActivity extends BaseActivity implements View.OnClickListener {

    private static final int MAX_NUM = 500;
    private DianZhongCommonTitle mCommonTitle;
    private LinearLayout mContentView;
    private int mOrientation;
    private TextView textViewOk;
    private TextView textViewNum;
    private EditText editText;
    private LinearLayout linearLayoutNote;

    private BookMarkNew mBookNote;

    /**
     * load
     *
     * @param context     context
     * @param orientation orientation
     * @param bookNote    bookNote
     */
    public static void launch(Context context, int orientation, BookMarkNew bookNote) {
        Intent intent = new Intent(context, ReaderNoteActivity.class);
        intent.putExtra("bookNote", bookNote);
        intent.putExtra("orientation", orientation);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }


    @Override
    public String getTagName() {
        return "ReaderNoteActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_note);
        setPadding();
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.commontitle);
        mContentView = findViewById(R.id.ll_content);
        linearLayoutNote = findViewById(R.id.ll_note_edit);
        textViewOk = findViewById(R.id.textView_ok);
        textViewNum = findViewById(R.id.textView_num);
        editText = findViewById(R.id.editText);
        TypefaceUtils.setHwChineseMediumFonts(textViewOk);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString();
                if (text.length() > MAX_NUM) {
                    text = text.substring(0, MAX_NUM);
                    editText.setText(text);
                    editText.setSelection(MAX_NUM);
                    textViewNum.setText(MAX_NUM + " / " + MAX_NUM);
                    textViewNum.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_FA2A2D));
                    linearLayoutNote.setBackgroundResource(R.drawable.bg_edit_note_full);
                } else {
                    if (text.length() > 30) {
                        textViewNum.setVisibility(View.VISIBLE);
                        textViewNum.setText(text.length() + " / " + MAX_NUM);
                        textViewNum.setTextColor(CompatUtils.getColor(getContext(), R.color.color_50_000000));

                    } else {
                        textViewNum.setVisibility(View.GONE);
                    }
                    linearLayoutNote.setBackgroundResource(R.drawable.bg_edit_note);
                }
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mBookNote = (BookMarkNew) intent.getSerializableExtra("bookNote");
            mOrientation = intent.getIntExtra("orientation", -1);
        }
        if (mBookNote != null && !TextUtils.isEmpty(mBookNote.noteText)) {
            editText.setText(mBookNote.noteText);
            editText.setSelection(mBookNote.noteText.length());
        }
    }

    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.textView_ok) {
            mBookNote.noteText = editText.getText().toString();
            BookMarkNew.addBookNote(this, mBookNote);
            EventBusUtils.sendMessage(new BookNoteEvent(BookNoteEvent.TYPE_ADD, mBookNote));

            SyncBookMarkService.launch(this);

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_note_save");
            paramsMap.put("cid", mBookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, mBookNote.bookId, paramsMap, null);

            finish();
        }
    }

    private void setPadding() {
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            int[] notchSize = HwUtils.getNotchSize();
            if (notchSize == null) {
                notchSize = new int[2];
                notchSize[0] = 0;
                notchSize[1] = 0;
            }
            mContentView.setPadding(notchSize[1], 0, 0, 0);
//            mCommonTitle.setPadding(notchSize[1], 0, 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        hideKeyboard();
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    @Override
    protected boolean needImmersionBar() {
        return true;
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        }
    }
}
