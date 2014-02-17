/*
 * Copyright 2011 tfdroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tinygame.lianliankan.screen;

import android.app.Activity;
import com.xstd.llk.R;

public class ScrollScreenActivity extends Activity {

	private ScrollScreen screen;
	private ScrollScreen.ScreenIndicator indicator;
	
	public ScrollScreenActivity() {
	}
	
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        screen = (ScrollScreen) findViewById(R.id.scroll_screen);
		
        if (screen == null) {
            throw new RuntimeException(
                    "Your content must have a ScrollScreen whose id attribute is " +
                    "'com.zhang.new_test.R.id.scroll_screen'");
        }
        
		indicator = (ScrollScreen.ScreenIndicator) findViewById(R.id.screen_indicator);
		if (indicator != null) {
			screen.setScreenIndicator(indicator);
		}
    }

    private void ensureScrollScreen() {
        if (screen == null) {
            this.setContentView(R.layout.screen_content);
        }
    }

	public ScrollScreen getScrollScreen() {
		ensureScrollScreen();
		return screen;
	}
}
