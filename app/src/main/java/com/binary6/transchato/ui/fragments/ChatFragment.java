package com.binary6.transchato.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binary6.transchato.R;
import com.binary6.transchato.core.chat.ChatContract;
import com.binary6.transchato.core.chat.ChatPresenter;
import com.binary6.transchato.events.PushNotificationEvent;
import com.binary6.transchato.models.Chat;
import com.binary6.transchato.ui.adapters.ChatRecyclerAdapter;
import com.binary6.transchato.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class ChatFragment extends Fragment implements ChatContract.View, TextView.OnEditorActionListener,View.OnClickListener,View.OnTouchListener,View.OnFocusChangeListener
{
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private boolean isEdit = false;
    private ProgressDialog mProgressDialog;
    public static final Pattern VALID_NAME_PATTERN_REGEX = Pattern.compile("[a-zA-Z_0-9]+$");

    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private Button mBSpace, mBdone, mBack, mBChange, mNum;
    private ChatPresenter mChatPresenter;


    private RelativeLayout mLayout, mKLayout;
    Gallery font_style_ga;
    private String sL[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
            "x", "y", "z", "Ã§", "Ã ", "Ã©", "Ã¨", "Ã»", "Ã®"};
    private String cL[] = { "ط", "ص", "ھ", "د", "ٹ", "پ", "ت", "ب", "ج", "ح",
            "م", "و", "ر", "ن", "ل", "ہ", "ا", "ک", "یی", "ق", "ف", "ے", "س",
            "ش", "غ", "ع", "ظ", "ض", "ذ", "ڈ", "ض","ۃ","خ","ژ","ز","ڑ","ں","ۂ","ء","آ","گ",
            "ي","ۓ","ؤ","ئ","چ","ث" };
    private String nS[] = {"!", ")", "'", "#", "3", "$", "%", "&", "8", "*",
            "?", "/", "+", "-", "9", "0", "1", "4", "@", "5", "7", "(", "2",
            "\"", "6", "_", "=", "]", "[", "<", ">", "|"};
    private Button mB[] = new Button[32];
    private int w, mWindowWidth;
    String fontstyle_drawable[] = {"COOPBL.TTF", "crystal radio kit.otf", "Feed The Bears.ttf", "murro.ttf",
            "Otaku Rant.ttf", "SamysBookifiedTuffy.ttf", "times.ttf", "ZnikomitNo25.otf"};

    private String mUpper = "upper", mLower = "lower";
    public static ChatFragment newInstance(String receiver,
                                           String receiverUid,
                                           String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);

        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
        setKeys(view);
        setFrow();
        setSrow();
        setTrow();
        setForow();
        mLayout = (RelativeLayout)view. findViewById(R.id.xK1);
        mKLayout = (RelativeLayout)view. findViewById(R.id.xKeyBoard);
        mETxtMessage.setOnTouchListener(this);
        mETxtMessage.setOnFocusChangeListener(this);
        mETxtMessage.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Constants.ARG_RECEIVER_UID));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }
    public static boolean isEnglishWord(String string) {
        return VALID_NAME_PATTERN_REGEX.matcher(string).find();
    }
    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        String receiver = getArguments().getString(Constants.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>(),getActivity());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());

        }
    }

    private void enableKeyboard() {

        mLayout.setVisibility(RelativeLayout.VISIBLE);
        mKLayout.setVisibility(RelativeLayout.VISIBLE);

    }

    private void disableKeyboard() {
        mLayout.setVisibility(RelativeLayout.INVISIBLE);
        mKLayout.setVisibility(RelativeLayout.INVISIBLE);

    }

    private void hideDefaultKeyboard() {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    private void setFrow() {
        w = (mWindowWidth / 13);
        w = w - 15;
        mB[16].setWidth(w);
        mB[22].setWidth(w + 3);
        mB[4].setWidth(w);
        mB[17].setWidth(w);
        mB[19].setWidth(w);
        mB[24].setWidth(w);
        mB[20].setWidth(w);
        mB[8].setWidth(w);
        mB[14].setWidth(w);
        mB[15].setWidth(w);
        mB[16].setHeight(50);
        mB[22].setHeight(50);
        mB[4].setHeight(50);
        mB[17].setHeight(50);
        mB[19].setHeight(50);
        mB[24].setHeight(50);
        mB[20].setHeight(50);
        mB[8].setHeight(50);
        mB[14].setHeight(50);
        mB[15].setHeight(50);

    }

    private void setSrow() {
        w = (mWindowWidth / 10);
        mB[0].setWidth(w);
        mB[18].setWidth(w);
        mB[3].setWidth(w);
        mB[5].setWidth(w);
        mB[6].setWidth(w);
        mB[7].setWidth(w);
        mB[26].setWidth(w);
        mB[9].setWidth(w);
        mB[10].setWidth(w);
        mB[11].setWidth(w);
        mB[26].setWidth(w);

        mB[0].setHeight(50);
        mB[18].setHeight(50);
        mB[3].setHeight(50);
        mB[5].setHeight(50);
        mB[6].setHeight(50);
        mB[7].setHeight(50);
        mB[9].setHeight(50);
        mB[10].setHeight(50);
        mB[11].setHeight(50);
        mB[26].setHeight(50);
    }

    private void setTrow() {
        w = (mWindowWidth / 12);
        mB[25].setWidth(w);
        mB[23].setWidth(w);
        mB[2].setWidth(w);
        mB[21].setWidth(w);
        mB[1].setWidth(w);
        mB[13].setWidth(w);
        mB[12].setWidth(w);
        mB[27].setWidth(w);
        mB[28].setWidth(w);
        mBack.setWidth(w);

        mB[25].setHeight(50);
        mB[23].setHeight(50);
        mB[2].setHeight(50);
        mB[21].setHeight(50);
        mB[1].setHeight(50);
        mB[13].setHeight(50);
        mB[12].setHeight(50);
        mB[27].setHeight(50);
        mB[28].setHeight(50);
        mBack.setHeight(50);

    }

    private void setForow() {
        w = (mWindowWidth / 10);
        mBSpace.setWidth(w * 4);
        mBSpace.setHeight(50);
        mB[29].setWidth(w);
        mB[29].setHeight(50);

        mB[30].setWidth(w);
        mB[30].setHeight(50);

        mB[31].setHeight(50);
        mB[31].setWidth(w);
        mBdone.setWidth(w + (w / 1));
        mBdone.setHeight(50);

    }
    private void setKeys(View c) {
        mWindowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth(); // getting
        // window
        // height
        // getting ids from xml files
        mB[0] = (Button)c. findViewById(R.id.xA);
        mB[1] = (Button) c.findViewById(R.id.xB);
        mB[2] = (Button) c.findViewById(R.id.xC);
        mB[3] = (Button) c.findViewById(R.id.xD);
        mB[4] = (Button) c.findViewById(R.id.xE);
        mB[5] = (Button) c.findViewById(R.id.xF);
        mB[6] = (Button) c.findViewById(R.id.xG);
        mB[7] = (Button) c.findViewById(R.id.xH);
        mB[8] = (Button) c.findViewById(R.id.xI);
        mB[9] = (Button) c.findViewById(R.id.xJ);
        mB[10] = (Button) c.findViewById(R.id.xK);
        mB[11] = (Button) c.findViewById(R.id.xL);
        mB[12] = (Button) c.findViewById(R.id.xM);
        mB[13] = (Button) c.findViewById(R.id.xN);
        mB[14] = (Button) c.findViewById(R.id.xO);
        mB[15] = (Button) c.findViewById(R.id.xP);
        mB[16] = (Button) c.findViewById(R.id.xQ);
        mB[17] = (Button) c.findViewById(R.id.xR);
        mB[18] = (Button) c.findViewById(R.id.xS);
        mB[19] = (Button) c.findViewById(R.id.xT);
        mB[20] = (Button) c.findViewById(R.id.xU);
        mB[21] = (Button) c.findViewById(R.id.xV);
        mB[22] = (Button) c.findViewById(R.id.xW);
        mB[23] = (Button) c.findViewById(R.id.xX);
        mB[24] = (Button) c.findViewById(R.id.xY);
        mB[25] = (Button) c.findViewById(R.id.xZ);
        mB[26] = (Button) c.findViewById(R.id.xS1);
        mB[27] = (Button) c.findViewById(R.id.xS2);
        mB[28] = (Button) c.findViewById(R.id.xS3);
        mB[29] = (Button) c.findViewById(R.id.xS4);
        mB[30] = (Button) c.findViewById(R.id.xS5);
        mB[31] = (Button) c.findViewById(R.id.xS6);
        mBSpace = (Button) c.findViewById(R.id.xSpace);
        mBdone = (Button) c.findViewById(R.id.xDone);
        mBChange = (Button) c.findViewById(R.id.xChange);
        mBack = (Button) c.findViewById(R.id.xBack);
        mNum = (Button) c.findViewById(R.id.xNum);
        for (int i = 0; i < mB.length; i++)
            mB[i].setOnClickListener(this);
        mBSpace.setOnClickListener(this);
        mBdone.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mBChange.setOnClickListener(this);
        mNum.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == mBChange) {

            if (mBChange.getTag().equals(mUpper)) {
                changeSmallLetters();
                changeSmallTags();
            } else if (mBChange.getTag().equals(mLower)) {
                changeCapitalLetters();
                changeCapitalTags();
            }

        } else if (v != mBdone && v != mBack && v != mBChange && v != mNum) {
            addText(v);

        } else if (v == mBdone) {

            disableKeyboard();
            sendMessage();
        } else if (v == mBack) {
            isBack(v);
        } else if (v == mNum) {
            String nTag = (String) mNum.getTag();
            if (nTag.equals("num")) {
                changeSyNuLetters();
                changeSyNuTags();
                mBChange.setVisibility(Button.INVISIBLE);

            }
            if (nTag.equals("ABC")) {
                changeCapitalLetters();
                changeCapitalTags();
            }

        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.edit_text_message && hasFocus == true) {
            isEdit = true;


        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.edit_text_message) {
            hideDefaultKeyboard();
            enableKeyboard();

        }
	/*	if (v == mEt1) {
			hideDefaultKeyboard();
			enableKeyboard();

		}*/
        return true;
    }
    private void addText(View v) {

        String b = "";
        b = (String) v.getTag();
        if (b != null) {
            // adding text in Edittext
            mETxtMessage.append(b);


        }

		/*if (isEdit1 == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {
				// adding text in Edittext
				text_edt.append(b);

			}*/


    }
    private void isBack(View v) {

        CharSequence cc = mETxtMessage.getText();
        if (cc != null && cc.length() > 0) {
            {
                mETxtMessage .setText("");
                mETxtMessage.append(cc.subSequence(0, cc.length() - 1));
            }


        }

    }
    private void changeSmallLetters() {
        mBChange.setVisibility(Button.VISIBLE);
        for (int i = 0; i < sL.length; i++)
            mB[i].setText(sL[i]);
        mNum.setTag("12#");
    }

    private void changeSmallTags() {
        for (int i = 0; i < sL.length; i++)
            mB[i].setTag(sL[i]);
        mBChange.setTag("lower");
        mNum.setTag("num");
    }

    private void changeCapitalLetters() {
        mBChange.setVisibility(Button.VISIBLE);
        for (int i = 0; i < cL.length; i++)
            mB[i].setText(cL[i]);
        mBChange.setTag("upper");
        mNum.setText("12#");

    }

    private void changeCapitalTags() {
        for (int i = 0; i < cL.length; i++)
            mB[i].setTag(cL[i]);
        mNum.setTag("num");

    }

    private void changeSyNuLetters() {

        for (int i = 0; i < nS.length; i++)
            mB[i].setText(nS[i]);
        mNum.setText("ABC");
    }

    private void changeSyNuTags() {
        for (int i = 0; i < nS.length; i++)
            mB[i].setTag(nS[i]);
        mNum.setTag("ABC");
    }

}
