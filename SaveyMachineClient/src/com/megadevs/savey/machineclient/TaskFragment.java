package com.megadevs.savey.machineclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.data.APIResponse;
import com.megadevs.savey.machinecommon.network.RealWebService;
import com.megadevs.savey.machinecommon.network.WebService;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TaskFragment extends BaseFragment implements View.OnClickListener, WebService.OnWebServiceResponse {

    public static final String EXTRA_API_RESPONSE = "api_response";

    public static TaskFragment getInstance(APIResponse response) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_API_RESPONSE, response);
        TaskFragment frag = new TaskFragment();
        frag.setArguments(args);
        return frag;
    }

    private static final int COUNTDOWN_TIMEOUT = 10;
    private static final long COUNTDOWN_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(COUNTDOWN_TIMEOUT);

    private ViewGroup imageAd;
    private ImageView image;
    private TextView txtCountdown;
    private Button btnSubmitAd;
    private ViewGroup surveyContainer;
    private TextView txtTitle;
    private RadioGroup answerContainer;
    private TextView txtCredit;
    private Button btnSubmitSurvey;

    private int machineId;
    private int taskId;
    private String[] answers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle data = getArguments();
        if (data == null) {
            Logg.e("Arguments can't be null");
            throw new IllegalStateException("Arguments can't be null");
        }
        APIResponse response = (APIResponse) data.getSerializable(EXTRA_API_RESPONSE);

        imageAd = (ViewGroup) view.findViewById(R.id.image_ad);
        image = (ImageView) view.findViewById(R.id.image);
        txtCountdown = (TextView) view.findViewById(R.id.txt_countdown);
        surveyContainer = (ViewGroup) view.findViewById(R.id.scroll_container);
        txtTitle = (TextView) view.findViewById(R.id.title);
        answerContainer = (RadioGroup) view.findViewById(R.id.answer_container);
        txtCredit = (TextView) view.findViewById(R.id.txt_credit);

        btnSubmitAd = (Button) view.findViewById(R.id.btn_submit_ad);
        btnSubmitSurvey = (Button) view.findViewById(R.id.btn_submit_survey);
        btnSubmitAd.setOnClickListener(this);
        btnSubmitSurvey.setOnClickListener(this);

        machineId = response.machine_id;
        taskId = response.task_id;
        answers = response.content;

        switch (response.type) {
            case survey:
                showSurvey(response);
                break;
            case ad:
                showAd(response);
                break;
        }
    }

    private void showAd(final APIResponse response) {
        txtCredit.setText(Html.fromHtml(String.format("Earn from this ad: <b>$ %.2f</b>", response.credit)));
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(response.content[0]);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                    if (bitmap != null) {
                        imageAd.setVisibility(View.VISIBLE);
                        surveyContainer.setVisibility(View.GONE);
                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(getMainActivity(), android.R.anim.fade_in));
                        startCountdown();
                        //TODO manage timer
                    }
                } catch (Exception e) {
                    Logg.e("Unable to show ad: %s", e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startCountdown() {
        new Thread() {
            @Override
            public void run() {
                int remaining = COUNTDOWN_TIMEOUT;
                try {
                    while (remaining > 0) {
                        final int secs = remaining;
                        getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtCountdown.setText(String.format("Wait %d seconds...", secs));
                            }
                        });
                        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                        remaining--;
                    }
                } catch (Exception e) {}
                getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCountdown.setVisibility(View.GONE);
                        btnSubmitAd.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();
    }

    private void showSurvey(APIResponse response) {
        txtCredit.setText(Html.fromHtml(String.format("Earn from this survey: <b>$ %.2f</b>", response.credit)));
        imageAd.setVisibility(View.GONE);
        surveyContainer.setVisibility(View.VISIBLE);
        txtTitle.setText(response.title);
        int i = 0;
        for (String answer : response.content) {
            RadioButton button = createRadioButton(answer, i++);
            answerContainer.addView(button);
        }
    }

    private RadioButton createRadioButton(String answer, int id) {
        RadioButton radio = new RadioButton(getMainActivity());
        radio.setText(answer);
        radio.setId(id);
        return radio;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_ad:
                submitAd();
                break;
            case R.id.btn_submit_survey:
                submitSurvey(answerContainer.getCheckedRadioButtonId());
                break;
        }
    }

    private void submitAd() {
        RealWebService.getInstance().sendTaskData(machineId, User.getInstance().getId(), taskId, null, this);
    }

    private void submitSurvey(int id) {
        if (id == -1) {
            return;
        }
        getMainActivity().showLoadingFragment(true);
        RealWebService.getInstance().sendTaskData(machineId, User.getInstance().getId(), taskId, answers[id], this);
    }

    @Override
    public void onWebServiceResponse(final APIResponse response) {
        if (response != null) {
            getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMainActivity().showTaskQrCode(response);
                }
            });
        }
    }
}
