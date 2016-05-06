package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.PREFS_NAME;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.THEME_SETTINGS;

/**
 * Created by Dominik Skoda on 06.05.2016.
 */
public class ThemeSpinnerListener implements AdapterView.OnItemSelectedListener {

    private Activity mActivity;

    private final String darkTheme;
    private final String lightTheme;
    private final String colorfulTheme;

    public ThemeSpinnerListener(Activity activity){
        mActivity = activity;

        darkTheme = mActivity.getString(R.string.dark_theme);
        lightTheme = mActivity.getString(R.string.light_theme);
        colorfulTheme = mActivity.getString(R.string.colorful_theme);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int newTheme = stringToTheme((String) parent.getItemAtPosition(position));

        if(MatrixActivity.currentTheme != newTheme) {
            SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(THEME_SETTINGS, newTheme);
            // Commit the edits!
            editor.commit();
            mActivity.recreate();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public String themeToString(int themeId){
        if(R.style.CustomThemeDark == themeId){
            return darkTheme;
        }
        if(R.style.CustomThemeLight == themeId){
            return lightTheme;
        }
        if(R.style.CustomThemeColorful == themeId){
            return colorfulTheme;
        }

        throw new UnsupportedOperationException(String.format("The theme ID \"%d\" not supported.", themeId));
    }

    public int stringToTheme(String themeName){
        if(darkTheme.equals(themeName)) {
            return R.style.CustomThemeDark;
        }
        if(lightTheme.equals(themeName)) {
            return R.style.CustomThemeLight;
        }
        if(colorfulTheme.equals(themeName)) {
            return R.style.CustomThemeColorful;
        }

        throw new UnsupportedOperationException(String.format("The \"%s\" theme name is not supported.", themeName));
    }
}
