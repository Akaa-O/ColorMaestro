package akaa.com.colormaestro.ui.main;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;

interface MainView {
    void launchActivityResult(Intent intent, int requestCode);
    void launchActivity(Intent intent);
    void animate(View view, Animation animation);
    void showAlertDialog(String message);
}
