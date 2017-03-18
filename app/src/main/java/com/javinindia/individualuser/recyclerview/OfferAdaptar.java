package com.javinindia.individualuser.recyclerview;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.javinindia.individualuser.R;
import com.javinindia.individualuser.apiparsing.offerListparsing.DetailsList;
import com.javinindia.individualuser.font.FontAsapBoldSingleTonClass;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.utility.Utility;
import com.javinindia.individualuser.volleycustomrequest.VolleySingleTon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ashish on 14-09-2016.
 */
public class OfferAdaptar extends RecyclerView.Adapter<OfferAdaptar.ViewHolder> {
    List<DetailsList> list;
    Context context;
    MyClickListener myClickListener;
    ArrayList<DetailsList> shopCategoryListArrayList;
    private int value = 0;

    public OfferAdaptar(Context context, int value) {
        this.context = context;
        this.value = value;
    }

    public void setData(List<DetailsList> list) {
        this.list = list;
        this.shopCategoryListArrayList = new ArrayList<>();
        this.shopCategoryListArrayList.addAll(list);
    }

    public List<DetailsList> getData() {
        return list;
    }


    @Override
    public OfferAdaptar.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final VolleySingleTon volleySingleTon = VolleySingleTon.getInstance(context);
        final DetailsList requestDetail = (DetailsList) list.get(position);

        viewHolder.txtShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickListener.onShopClick(position,requestDetail);
            }
        });

        if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopOwnerName().trim())) {
            String ownName = requestDetail.getOfferShopDetails().getShopOwnerName().trim();
            viewHolder.txtShopName.setText(Utility.fromHtml("Sold by: "+ownName));
            if (value == 0) {
                viewHolder.txtShopName.setVisibility(View.VISIBLE);
            } else if (value == 1) {
                viewHolder.txtShopName.setVisibility(View.VISIBLE);
            } else if (value == 2) {
                viewHolder.txtShopName.setVisibility(View.GONE);
            } else {
                viewHolder.txtShopName.setVisibility(View.GONE);
            }
        } else {
            viewHolder.txtShopName.setText(Utility.fromHtml("Shop not found"));
        }


        if (!TextUtils.isEmpty(requestDetail.getOfferMallDetails().getMallName().trim())) {
            String mallName = requestDetail.getOfferMallDetails().getMallName().trim();
            viewHolder.txtMallName.setText(Utility.fromHtml(mallName));
            if (value == 0) {
                viewHolder.txtMallName.setVisibility(View.GONE);
            } else if (value == 1) {
                viewHolder.txtMallName.setVisibility(View.GONE);
            } else if (value == 2) {
                viewHolder.txtMallName.setVisibility(View.GONE);
            } else {
                viewHolder.txtMallName.setVisibility(View.GONE);
            }
        } else {
            viewHolder.txtMallName.setText(Utility.fromHtml("Mall not found"));
        }

        /*viewHolder.txtMallName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickListener.onMallClick(position,requestDetail);
            }
        });*/

        if (!TextUtils.isEmpty(requestDetail.getOfferBrandDetails().getBrandName().trim())) {
            String brandName = requestDetail.getOfferBrandDetails().getBrandName().trim();
        }

        final ArrayList<String> data = new ArrayList<>();
        if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopNo().trim())) {
            String Address = requestDetail.getOfferShopDetails().getShopNo().trim();
            data.add(Address);
        }
        if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopCity().trim())) {
            String city = requestDetail.getOfferShopDetails().getShopCity().trim();
            data.add(city);
        }
       /* if (!TextUtils.isEmpty(requestDetail.getOfferMallDetails().getMallCity().trim())) {
            String mallCity = requestDetail.getOfferMallDetails().getMallCity().trim();
            data.add(mallCity);
        }*/
        if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopState().trim())) {
            String State = requestDetail.getOfferShopDetails().getShopState().trim();
            data.add(State);
        }
        if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopFloorNo().trim())) {
            String PinCode = requestDetail.getOfferShopDetails().getShopFloorNo().trim();
            data.add(PinCode);
        }

        if (data.size() > 0) {
            String str = Arrays.toString(data.toArray());
            String test = str.replaceAll("[\\[\\](){}]", "");
            viewHolder.txtAddress.setText(Utility.fromHtml(test));
            if (value == 0) {
                viewHolder.txtAddress.setVisibility(View.GONE);
            } else if (value == 1) {
                viewHolder.txtAddress.setVisibility(View.GONE);
            } else if (value == 2) {
                viewHolder.txtAddress.setVisibility(View.GONE);
            } else {
                viewHolder.txtAddress.setVisibility(View.GONE);
            }
        } else {
            viewHolder.txtAddress.setText("Address: Not found");
        }


        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferTitle().trim())) {
            String offerTitle = requestDetail.getOfferDetails().getOfferTitle().trim();
            viewHolder.txtOfferTitle.setText(Utility.fromHtml(offerTitle));
        } else {
            viewHolder.txtOfferTitle.setText(Utility.fromHtml("Offer not found"));
        }


        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferOpenDate().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferCloseDate().trim())) {
            String openTime = requestDetail.getOfferDetails().getOfferOpenDate().trim();
            String closeTime = requestDetail.getOfferDetails().getOfferCloseDate().trim();
            viewHolder.txtTimingOffer.setText(openTime + " till " + closeTime);
        } else {
            viewHolder.txtTimingOffer.setText("Timing not available");
        }

        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentageType().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentage().trim())) {
            String offerType = requestDetail.getOfferDetails().getOfferPercentageType().trim();
            String offerPercent = requestDetail.getOfferDetails().getOfferPercentage().trim();
            viewHolder.txtUPTO.setText(offerType);
            viewHolder.txtOfferPercent.setText(offerPercent + "% off");
        } else if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferActualPrice().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferDiscountedPrice().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentageType().trim()) && TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentage().trim())) {
            String offerActualPrice = requestDetail.getOfferDetails().getOfferActualPrice().trim();
            String offerDiscountPrice = requestDetail.getOfferDetails().getOfferDiscountedPrice().trim();
            String offerType = requestDetail.getOfferDetails().getOfferPercentageType().trim();
            double actual = Double.parseDouble(offerActualPrice);
            double discount = Double.parseDouble(offerDiscountPrice);
            int percent = (int) (100 - (discount * 100.0f) / actual);
            viewHolder.txtUPTO.setText(offerType);
            viewHolder.txtOfferPercent.setText(percent + "% off");
        } else if (TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentageType().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferPercentage().trim())) {
            viewHolder.txtUPTO.setText("UPTO");
            viewHolder.txtOfferPercent.setText(requestDetail.getOfferDetails().getOfferPercentage().trim() + "% off");
        } else {
            if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferActualPrice().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferDiscountedPrice().trim())) {
                String offerActualPrice = requestDetail.getOfferDetails().getOfferActualPrice().trim();
                String offerDiscountPrice = requestDetail.getOfferDetails().getOfferDiscountedPrice().trim();
                double actual = Double.parseDouble(offerActualPrice);
                double discount = Double.parseDouble(offerDiscountPrice);
                int percent = (int) (100 - (discount * 100.0f) / actual);
                viewHolder.txtUPTO.setText("UPTO");
                viewHolder.txtOfferPercent.setText(percent + "% off");
            }

        }
        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferActualPrice().trim()) && !TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferDiscountedPrice().trim())) {
            String offerActualPrice = requestDetail.getOfferDetails().getOfferActualPrice().trim();
            String offerDiscountPrice = requestDetail.getOfferDetails().getOfferDiscountedPrice().trim();
            //  viewHolder.llOffItem.setBackgroundColor(Color.parseColor("#1da6b9"));
            viewHolder.txtActualPrice.setText(Utility.fromHtml("\u20B9" + offerActualPrice + "/-"));
            viewHolder.txtActualPrice.setPaintFlags(viewHolder.txtActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.txtDiscountPrice.setText(Utility.fromHtml("\u20B9" + offerDiscountPrice + "/-"));
        }

        if (requestDetail.getFavStatus() == 1) {
            viewHolder.chkImageOffer.setChecked(true);
        } else {
            viewHolder.chkImageOffer.setChecked(false);
        }

        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferBanner().trim())) {
            String offerBanner = requestDetail.getOfferDetails().getOfferBanner().trim();
            Utility.imageLoadGlideLibrary(context, viewHolder.progressBar, viewHolder.imgShopLogoOffer, offerBanner);
        } else if (!TextUtils.isEmpty(requestDetail.getOfferBrandDetails().getBrandLogo().trim())) {
            String brandLogo = requestDetail.getOfferBrandDetails().getBrandLogo().trim();
            Utility.imageLoadGlideLibrary(context, viewHolder.progressBar, viewHolder.imgShopLogoOffer, brandLogo);
        } else if (!TextUtils.isEmpty(requestDetail.getOfferShopDetails().getShopProfilePic().trim())) {
            String shopLogo = requestDetail.getOfferShopDetails().getShopProfilePic().trim();
            Utility.imageLoadGlideLibrary(context, viewHolder.progressBar, viewHolder.imgShopLogoOffer, shopLogo);
        } else {
            viewHolder.imgShopLogoOffer.setImageResource(R.drawable.no_image_icon);
        }

        if (!TextUtils.isEmpty(requestDetail.getOfferDetails().getOfferCategory().trim())) {
            String category = requestDetail.getOfferDetails().getOfferCategory().trim();
            viewHolder.txtOfferCategory.setText("on " + Utility.fromHtml(category));
        } else {
            //  viewHolder.txtOfferCategory.setText(Html.fromHtml("Category not found"));
        }

        viewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onOfferItemClick(position, requestDetail);
            }
        });

        viewHolder.chkImageOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onFavoriteClick(position, requestDetail);
            }
        });

        //   viewHolder.ratingBar.setRating(Float.parseFloat("2.0"));


    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView txtShopName, txtAddress, txtTimingOffer, txtOfferTitle, txtOfferCategory, txtMallName, txtActualPrice, txtDiscountPrice, txtOfferPercent, txtMRP,txtUPTO,txtOFFER;
        public CardView rlMain;
        public CheckBox chkImageOffer;
        ImageView imgShopLogoOffer;
        public ProgressBar progressBar;
        LinearLayout llOffItem;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rlMain = (CardView) itemLayoutView.findViewById(R.id.rlMain);
            progressBar = (ProgressBar) itemLayoutView.findViewById(R.id.progress);
            imgShopLogoOffer = (ImageView) itemLayoutView.findViewById(R.id.imgShopLogoOffer);
            txtShopName = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtShopName);
            txtShopName.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtMallName = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtMallName);
            txtMallName.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtAddress = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtAddress);
            txtAddress.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtTimingOffer = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtTimingOffer);
            txtTimingOffer.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtOfferTitle = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtOfferTitle);
            txtOfferTitle.setTypeface(FontAsapBoldSingleTonClass.getInstance(context).getTypeFace());
            txtOfferCategory = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtOfferCategory);
            txtOfferCategory.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            //  ratingBar = (RatingBar) itemLayoutView.findViewById(R.id.ratingBar);
            rlMain = (CardView) itemLayoutView.findViewById(R.id.rlMain);
            chkImageOffer = (CheckBox) itemLayoutView.findViewById(R.id.chkImageOffer);
            llOffItem = (LinearLayout) itemLayoutView.findViewById(R.id.llOffItem);
            txtMRP = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtMRP);
            txtMRP.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtUPTO = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtUPTO);
            txtUPTO.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtOFFER = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtOFFER);
            txtOFFER.setTypeface(FontAsapBoldSingleTonClass.getInstance(context).getTypeFace());
            txtActualPrice = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtActualPrice);
            txtActualPrice.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
            txtDiscountPrice = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtDiscountPrice);
            txtDiscountPrice.setTypeface(FontAsapBoldSingleTonClass.getInstance(context).getTypeFace());
            txtOfferPercent = (AppCompatTextView) itemLayoutView.findViewById(R.id.txtOfferPercent);
            txtOfferPercent.setTypeface(FontAsapRegularSingleTonClass.getInstance(context).getTypeFace());
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public interface MyClickListener {
        void onOfferItemClick(int position, DetailsList detailsList);

        void onFavoriteClick(int position, DetailsList detailsList);

        void onShopClick(int position, DetailsList detailsList);

        void onMallClick(int position, DetailsList detailsList);
    }

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(shopCategoryListArrayList);
        } else {
            for (DetailsList thankFulDetail : shopCategoryListArrayList) {
                if (thankFulDetail.getOfferBrandDetails().getBrandName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    list.add(thankFulDetail);
                }
            }
        }
        notifyDataSetChanged();
    }
}