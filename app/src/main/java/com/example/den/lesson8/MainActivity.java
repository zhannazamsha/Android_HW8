package com.example.den.lesson8;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.den.lesson8.DataSources.Giphy.NetworkingManagerGiphy;
import com.example.den.lesson8.DataSources.Local.NetworkingManagerLocal;
import com.example.den.lesson8.DataSources.Unsplash.NetworkingManagerUnsplash;
import com.example.den.lesson8.Interfaces.NetworkingManager;
import com.example.den.lesson8.Interfaces.PhotoItem;
import com.example.den.lesson8.Interfaces.PhotoItemsPresenter;
import com.example.den.lesson8.Interfaces.PhotoItemsPresenterCallbacks;
import com.example.den.lesson8.RecyclerView.PhotoPresenterRecyclerView;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends Activity implements PhotoItemsPresenterCallbacks {

    public enum ImgServices {
        UNSPLASH,
        GIPHY,
        FAVORITE
    }

    private NetworkingManager networkingManager;
    private PhotoItemsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isOpenedBefore = prefs.getBoolean(getString(R.string.first_open), false);
        if(!isOpenedBefore) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.first_open), Boolean.TRUE);
            edit.commit();
            Notifications.showNotificationAfterDelay(Notifications.getNotification(this), this);
        }
        Push.push(this);
        showImgService(ImgServices.GIPHY);


      //      throw new RuntimeException("This is a crash");


    }

    private void showImgService(ImgServices service) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "selected");
        switch (service) {
            case GIPHY:
                FirebaseAnalytics.getInstance(this).
                        logEvent(ImgServices.GIPHY.name(), bundle);
                networkingManager = new NetworkingManagerGiphy();
                break;
            case UNSPLASH:
                FirebaseAnalytics.getInstance(this).
                        logEvent(ImgServices.UNSPLASH.name(), bundle);
                networkingManager = new NetworkingManagerUnsplash();
                break;
            case FAVORITE:
                FirebaseAnalytics.getInstance(this).
                        logEvent(ImgServices.FAVORITE.name(), bundle);
                networkingManager = new NetworkingManagerLocal();
        }

       // this.presenter = new PhotoItemPresenterGridView();
        this.presenter = new PhotoPresenterRecyclerView();

        this.networkingManager.getPhotoItems(photoItems ->
                runOnUiThread(()-> {
                    presenter.setupWithPhotoItems(photoItems,this, this);
                })
        );
    }

    @Override
    public void onItemSelected(PhotoItem item) {
        Intent shareIntent = new Intent(this, ShareActivityWithFragments.class);
        shareIntent.putExtra(ShareActivity.PHOTO_ITEM_KEY,item);
        startActivity(shareIntent);
    }

    @Override
    public void onItemToggleFavorite(PhotoItem item) {
        testFavoriteORM(item);
    }

    @Override
    public void onLastItemReach(int position) {
        networkingManager.fetchNewItemsFromPosition(position, photoItems -> {
            runOnUiThread(()-> {
                presenter.updateWithItems(photoItems);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);

        final MenuItem favoriteMenuItem = menu.findItem(R.id.action_show_favotites);
        favoriteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showImgService(ImgServices.FAVORITE);
                return true;
            }
        });

        final MenuItem showUnsplashMenuItem = menu.findItem(R.id.action_show_unslash);
        showUnsplashMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showImgService(ImgServices.UNSPLASH);
                return true;
            }
        });

        final MenuItem showUnsplashGiphyItem = menu.findItem(R.id.action_show_giphy);
        showUnsplashGiphyItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showImgService(ImgServices.GIPHY);
                return true;
            }
        });

        return true;
    }

    // *****************************************************
    // *****************************************************
    // ************************ ORM ************************
    // *****************************************************
    // *****************************************************

    private void testFavoriteORM(PhotoItem item) {

        if(item.isSavedToDatabase()) {
            item.deleteFromDatabase();

            // Remove favorite from screen if unfavorite from favorite screen
            if (networkingManager.getClass() == NetworkingManagerLocal.class) {
                showImgService(ImgServices.FAVORITE);
            }
        } else {
            item.saveToDatabase();
        }
    }
}
