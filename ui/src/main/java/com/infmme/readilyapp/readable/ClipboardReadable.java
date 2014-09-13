package com.infmme.readilyapp.readable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

/**
 * Created by infm on 6/12/14. Enjoy ;)
 */
public class ClipboardReadable extends Readable {

	private ClipboardManager clipboard;
	private boolean oncePasted;

	public ClipboardReadable(){
		type = TYPE_CLIPBOARD;
	}

	public ClipboardReadable(ClipboardReadable that){
		super(that);
		clipboard = that.getClipboard();
		oncePasted = that.isOncePasted();
	}

	public boolean isOncePasted(){
		return oncePasted;
	}

	public ClipboardManager getClipboard(){
		return clipboard;
	}

	public void process(final Context context){
		Looper.prepare(); //TODO: review it CAREFULLY
		clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		processFailed = !clipboard.hasText();
		processed = true;
	}

	@Override
	public void readData(){
		if (!oncePasted)
			text.append(paste(clipboard));
		else
			setText("");
	}

	@Override
	public Readable getNext(){
		ClipboardReadable result = new ClipboardReadable(this);
		result.readData();
		return result;
	}

	private String paste(ClipboardManager clipboard){
		oncePasted = true;
		ClipData clipData = clipboard.getPrimaryClip();
		if (clipData != null && clipData.getItemCount() > 0){
			ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
			CharSequence pasteData = item.getText();
			if (pasteData != null){
				return pasteData.toString();
			}
		}
		return null;
	}
}