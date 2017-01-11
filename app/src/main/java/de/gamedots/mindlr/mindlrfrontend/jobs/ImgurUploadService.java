package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.ImageResponse;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.util.ImgurApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;
import static de.gamedots.mindlr.mindlrfrontend.util.ImgurApi.IMAGE_KEY;

/**
 * Service to upload and image to imgur.
 */

public class ImgurUploadService {

    private WeakReference<Context> _context;
    private Uri _draftUri;
    private JSONObject _content;

    public ImgurUploadService(Context context, Uri draftUri, JSONObject content) {
        _context = new WeakReference<>(context);
        _draftUri = draftUri;
        _content = content;
    }

    public void start(final Uri fileUri) {

        new AsyncTask<Uri, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Uri... params) {
                Bitmap bm = null;
                try {
                    bm = Glide.with(_context.get())
                            .loadFromMediaStore(params[0])
                            .asBitmap().into(SIZE_ORIGINAL, SIZE_ORIGINAL).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bm) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ImgurApi.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                if (bm != null) {
                    //final RequestBody requestBody = RequestBody.create(MEDIA_TYPE, encodeImage(bm));
                    Map<String, String> imgMap = new HashMap<String, String>();
                    imgMap.put(IMAGE_KEY, encodeImage(bm));

                    ImgurApi imageService = retrofit.create(ImgurApi.class);

                    // start asynchronous request
                    Call<ImageResponse> call = imageService.postImage(ImgurApi.AUTH_DATA, imgMap);
                    call.enqueue(new Callback<ImageResponse>() {
                        @Override
                        public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                            ImageResponse image = null;
                            boolean result = false;

                            if (response.isSuccessful()) {
                                // imgur upload succeeded grab image URL and start upload to backend
                                image = response.body();
                                result = true;

                                Log.v(LOG.AUTH, image.data.link);
                            }
                            handleResult(result, image);
                        }

                        @Override
                        public void onFailure(Call<ImageResponse> call, Throwable t) {
                            handleResult(false, null);
                            Log.v(LOG.AUTH, t.getMessage());
                        }
                    });
                }
            }
        }.execute(fileUri);
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] b = out.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void handleResult(boolean success, ImageResponse image){
        EventBus.getDefault()
                .post(new ImageUploadResult(
                        success,
                        (image == null) ? "" : image.data.link,
                        _content,
                        _draftUri)
                );
    }
}
