/***
 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.bridginggood.Biz;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class BizMapOverlayItem extends OverlayItem {

	protected String mImageURL;
	protected String mBizId;
	
	public BizMapOverlayItem(GeoPoint point, String title, String snippet, String imageURL, String bizId) {
		super(point, title, snippet);
		mImageURL = imageURL;
		mBizId = bizId;
	}

	public String getImageURL() {
		return mImageURL;
	}
	
	public String getBizId(){
		return mBizId;
	}

	public void setImageURL(String imageURL) {
		this.mImageURL = imageURL;
	}
	
}
