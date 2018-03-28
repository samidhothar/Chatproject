package com.delaroystudios.firebasechat.ui.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.delaroystudios.firebasechat.APICalls;
import com.delaroystudios.firebasechat.Constants;
import com.delaroystudios.firebasechat.R;
import com.delaroystudios.firebasechat.TranslateResponse;
import com.delaroystudios.firebasechat.models.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

import static com.delaroystudios.firebasechat.ui.fragments.ChatFragment.VALID_NAME_PATTERN_REGEX;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    public static final Pattern VALID_NAME_PATTERN_REGEX = Pattern.compile("[a-zA-Z_0-9]+$");

    private List<Chat> mChats;
    Activity activity;

    public ChatRecyclerAdapter(List<Chat> chats,Activity activity) {
        mChats = chats;
        this.activity = activity;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(final MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);
        if(mChats.size()-1==position){
           /* if (isEnglishWord(chat.message)){
                translateText(chat.message,"ur-en",myChatViewHolder.txtChatMessage);

            }else {
                translateText(chat.message,"en-ur",myChatViewHolder.txtChatMessage);

            }*/
           myChatViewHolder.txtChatMessage.setText(chat.message);

        }else {
            myChatViewHolder.txtChatMessage.setText(chat.message);
        }
        myChatViewHolder.txtChatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setcopytext(myChatViewHolder.txtChatMessage.getText().toString(),"sami");
                Toast.makeText(activity,"Text copyed",Toast.LENGTH_LONG).show();
            }
        });
        myChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);
        if (isEnglishWord(chat.message)){
            Log.e("message",chat.message+"");
            Log.e("urdu ","english");
            translateText(chat.message,"en-ur",otherChatViewHolder.txtChatMessage);

        }else {
            Log.e("english","urdu");
            Log.e("message",chat.message+"");

            translateText(chat.message,"ur-en",otherChatViewHolder.txtChatMessage);

        }
        //otherChatViewHolder.txtChatMessage.setText(chat.message);
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }
    private void translateText(final String text, final String lang, final TextView message) {

        class getQData extends AsyncTask<Void, Void, Void> {
        String text_to_return ="";
           // ProgressDialog loading;
            String ROOT_URL = activity.getResources().getString(R.string.ROOT_URL);

            Retrofit adapter = new Retrofit.Builder()
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
            APICalls api = adapter.create(APICalls.class);

            @Override
            protected Void doInBackground(Void... voids) {
                String key = Constants.MY_KEY;
                Log.e("key",key+"|"+lang);
                String lluange = lang;
                Call<TranslateResponse> call = api.translate(key, text, lluange);
                call.enqueue(new Callback<TranslateResponse>() {
                    @Override
                    public void onResponse(retrofit.Response<TranslateResponse> response, Retrofit retrofit) {
                        //loading.dismiss();
                        //  hidePD();
                        Log.d("succ", "onResponse:code" + String.valueOf(response.code()));
                        Log.d("error mesg", String.valueOf(response.message()));
                        switch (response.code()) {
                            case 200:
                                TranslateResponse tr = response.body();
                                text_to_return = tr.getText().get(0);
                                message.setText(text_to_return);
                                //String currentFromR = reverseCurrentFrom(currentFrom);
                                //ConvertTextToSpeech(currentFromR, toText.getText().toString());
                                break;
                            default:

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        //  pd.dismiss();
                        Log.e("retro error", t.getMessage());


                    }
                });
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // showPD();

            }

          /*Override  @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {
           //     text_to_return = "";



            }*/
        }

        getQData ru = new getQData();
        try {
            ru.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    public static boolean isEnglishWord(String string) {
        return VALID_NAME_PATTERN_REGEX.matcher(string).find();
    }
    public void setcopytext(String text,String label){
        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}
