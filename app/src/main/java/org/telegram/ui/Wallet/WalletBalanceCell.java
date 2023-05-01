/*
 * This is the source code of Wallet for Android v. 1.0.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Nikolai Kudashov, 2019-2020.
 */

package org.telegram.ui.Wallet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.TonController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;

@SuppressWarnings("FieldCanBeLocal")
public class WalletBalanceCell extends FrameLayout {

    private SimpleTextView valueTextView;
    private TextView yourBalanceTextView;
    private FrameLayout receiveButton;
    private FrameLayout sendButton;
    private FrameLayout buyButton;
    private FrameLayout sellButton;
    private SimpleTextView receiveTextView;
    private SimpleTextView sendTextView;
    private SimpleTextView buyTextView;
    private SimpleTextView sellTextView;
    private Drawable sendDrawable;
    private Drawable receiveDrawable;
    private Typeface defaultTypeFace;
    private RLottieDrawable gemDrawable;

    public WalletBalanceCell(Context context) {
        super(context);

        valueTextView = new SimpleTextView(context);
        valueTextView.setTextColor(Theme.getColor(Theme.key_wallet_whiteText));
        valueTextView.setTextSize(41);
        valueTextView.setDrawablePadding(AndroidUtilities.dp(7));
        valueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 10, 0, 0));

        gemDrawable = new RLottieDrawable(R.raw.wallet_gem, "" + R.raw.wallet_gem, AndroidUtilities.dp(42), AndroidUtilities.dp(42), false);
        gemDrawable.setAutoRepeat(1);
        gemDrawable.setAllowDecodeSingleFrame(true);
        gemDrawable.addParentView(valueTextView);
        valueTextView.setRightDrawable(gemDrawable);
        gemDrawable.start();

        yourBalanceTextView = new TextView(context);
        yourBalanceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        yourBalanceTextView.setTextColor(Theme.getColor(Theme.key_wallet_whiteText));
        defaultTypeFace = yourBalanceTextView.getTypeface();
        addView(yourBalanceTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 60, 0, 0));

        receiveDrawable = context.getResources().getDrawable(R.drawable.wallet_receive).mutate();
        receiveDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_wallet_buttonText), PorterDuff.Mode.MULTIPLY));
        sendDrawable = context.getResources().getDrawable(R.drawable.wallet_send).mutate();
        sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_wallet_buttonText), PorterDuff.Mode.MULTIPLY));

        for (int a = 0; a < 3; a++) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), Theme.getColor(Theme.key_wallet_buttonBackground), Theme.getColor(Theme.key_wallet_buttonPressedBackground)));
            addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 42, Gravity.LEFT | Gravity.TOP, a == 0 ? 32 : 16, 168, 0, 0));
            frameLayout.setOnClickListener(v -> {
                if (v == receiveButton) {
                    onReceivePressed();
                } else if (v == sendButton){
                    onSendPressed();
                } else if (v == buyButton) {
                    onBuyPressed();
                } else {
                    onSellPressed();
                }
            });

            SimpleTextView buttonTextView = new SimpleTextView(context);
            buttonTextView.setTextColor(Theme.getColor(Theme.key_wallet_buttonText));
            buttonTextView.setTextSize(14);
            buttonTextView.setDrawablePadding(AndroidUtilities.dp(6));
            buttonTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            if (a == 1) {
                buttonTextView.setText(LocaleController.getString("WalletReceive", R.string.WalletReceive));
                buttonTextView.setLeftDrawable(receiveDrawable);
                receiveTextView = buttonTextView;
                receiveButton = frameLayout;
            } else if (a == 2) {
                buttonTextView.setText(LocaleController.getString("WalletSend", R.string.WalletSend));
                buttonTextView.setLeftDrawable(sendDrawable);
                sendTextView = buttonTextView;
                sendButton = frameLayout;
            } else if (a == 0) {
                buttonTextView.setText(LocaleController.getString("WalletBuy", R.string.WalletBuy));
                buyTextView = buttonTextView;
                buyButton = frameLayout;
            } else {
                buttonTextView.setText(LocaleController.getString("WalletSell", R.string.WalletSell));
                sellTextView = buttonTextView;
                sellButton = frameLayout;
            }
            frameLayout.addView(buttonTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int buttonWidth;
        if (sendButton.getVisibility() == VISIBLE) {
            buttonWidth = (width - AndroidUtilities.dp(96)) / 3;
        } else {
            buttonWidth = (width - AndroidUtilities.dp(80)) / 2;
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) buyButton.getLayoutParams();
        layoutParams.width = buttonWidth;

        layoutParams = (FrameLayout.LayoutParams) receiveButton.getLayoutParams();
        layoutParams.width = buttonWidth;
        layoutParams.leftMargin = AndroidUtilities.dp(16 + 32) + buttonWidth;

        layoutParams = (FrameLayout.LayoutParams) sendButton.getLayoutParams();
        layoutParams.width = buttonWidth;
        layoutParams.leftMargin = AndroidUtilities.dp(16 + 16 + 32) + 2 * buttonWidth;

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(236 + 6), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gemDrawable.stop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        gemDrawable.start();
    }

    public void setBalance(long balance, long unlockedBalance, boolean rWallet, boolean test) {
        if (balance >= 0) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(TonController.formatCurrency(rWallet ? unlockedBalance : balance));
            int index = TextUtils.indexOf(stringBuilder, '.');
            if (index >= 0) {
                stringBuilder.setSpan(new TypefaceSpan(defaultTypeFace, AndroidUtilities.dp(27)), index + 1, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            valueTextView.setText(stringBuilder);
            valueTextView.setTranslationX(0);
            yourBalanceTextView.setVisibility(VISIBLE);
            if (rWallet && unlockedBalance != balance) {
                String str = LocaleController.formatString("WalletLockedBalance", R.string.WalletLockedBalance, TonController.formatCurrency(balance - unlockedBalance));
                SpannableStringBuilder builder = new SpannableStringBuilder(str);
                int idx = str.indexOf('*');
                if (idx >= 0) {
                    builder.setSpan(new ImageSpan(getContext(), R.drawable.gem_s, DynamicDrawableSpan.ALIGN_BOTTOM) {
                        @Override
                        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                            Drawable b = getDrawable();
                            canvas.save();
                            int transY = (bottom - top) / 2 - b.getBounds().height() / 2;
                            canvas.translate(x, transY);
                            b.draw(canvas);
                            canvas.restore();
                        }
                    }, idx, idx + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                yourBalanceTextView.setText(builder);
            } else {
                if (test) {
                    yourBalanceTextView.setText(LocaleController.getString("WalletYourTestBalance", R.string.WalletYourTestBalance));
                } else {
                    yourBalanceTextView.setText(LocaleController.getString("WalletYourBalance", R.string.WalletYourBalance));
                }
            }
        } else {
            valueTextView.setText("");
            valueTextView.setTranslationX(-AndroidUtilities.dp(4));
            yourBalanceTextView.setVisibility(GONE);
        }
        int visibility = balance <= 0 ? GONE : VISIBLE;
        if (sendButton.getVisibility() != visibility) {
            sendButton.setVisibility(visibility);
//            sellButton.setVisibility(visibility);
        }
    }

    protected void onReceivePressed() {

    }

    protected void onSendPressed() {

    }

    protected void onBuyPressed() {

    }

    protected void onSellPressed() {

    }
}
