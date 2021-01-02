package awais.instagrabber.adapters.viewholder.directmessages;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;

import awais.instagrabber.R;
import awais.instagrabber.databinding.LayoutDmBaseBinding;
import awais.instagrabber.databinding.LayoutDmRavenMediaBinding;
import awais.instagrabber.interfaces.MentionClickListener;
import awais.instagrabber.models.ProfileModel;
import awais.instagrabber.models.enums.MediaItemType;
import awais.instagrabber.models.enums.RavenMediaViewMode;
import awais.instagrabber.repositories.responses.directmessages.DirectItem;
import awais.instagrabber.repositories.responses.directmessages.DirectItemMedia;
import awais.instagrabber.repositories.responses.directmessages.DirectItemVisualMedia;
import awais.instagrabber.repositories.responses.directmessages.DirectThread;
import awais.instagrabber.repositories.responses.directmessages.ImageVersions2;
import awais.instagrabber.utils.NumberUtils;
import awais.instagrabber.utils.ResponseBodyUtils;
import awais.instagrabber.utils.Utils;

public class DirectItemRavenMediaViewHolder extends DirectItemViewHolder {

    private final LayoutDmRavenMediaBinding binding;
    private final int maxHeight;
    private final int maxWidth;
    private final int dmRadius;
    private final int dmRadiusSmall;

    public DirectItemRavenMediaViewHolder(@NonNull final LayoutDmBaseBinding baseBinding,
                                          @NonNull final LayoutDmRavenMediaBinding binding,
                                          final ProfileModel currentUser,
                                          final DirectThread thread,
                                          final MentionClickListener mentionClickListener,
                                          final View.OnClickListener onClickListener) {
        super(baseBinding, currentUser, thread, onClickListener);
        this.binding = binding;
        final Resources resources = itemView.getResources();
        maxHeight = resources.getDimensionPixelSize(R.dimen.dm_media_img_max_height);
        final int margin = resources.getDimensionPixelSize(R.dimen.dm_message_item_margin);
        maxWidth = Utils.displayMetrics.widthPixels - margin - Utils.convertDpToPx(8);
        dmRadius = resources.getDimensionPixelSize(R.dimen.dm_message_card_radius);
        dmRadiusSmall = resources.getDimensionPixelSize(R.dimen.dm_message_card_radius_small);
        setItemView(binding.getRoot());
    }

    @Override
    public void bindItem(final DirectItem directItemModel, final MessageDirection messageDirection) {
        removeBg();
        final DirectItemVisualMedia visualMedia = directItemModel.getVisualMedia();
        final DirectItemMedia media = visualMedia.getMedia();
        if (media == null) return;
        setExpiryInfo(visualMedia);
        setPreview(visualMedia, messageDirection);
        /*final boolean isExpired = visualMedia == null || (mediaModel = visualMedia.getMedia()) == null ||
                TextUtils.isEmpty(mediaModel.getThumbUrl()) && mediaModel.getPk() < 1;

        RavenExpiringMediaActionSummary mediaActionSummary = null;
        if (visualMedia != null) {
            mediaActionSummary = visualMedia.getExpiringMediaActionSummary();
        }
        binding.mediaExpiredIcon.setVisibility(isExpired ? View.VISIBLE : View.GONE);

        int textRes = R.string.dms_inbox_raven_media_unknown;
        if (isExpired) textRes = R.string.dms_inbox_raven_media_expired;

        if (!isExpired) {
            if (mediaActionSummary != null) {
                final ActionType expiringMediaType = mediaActionSummary.getType();

                if (expiringMediaType == ActionType.DELIVERED)
                    textRes = R.string.dms_inbox_raven_media_delivered;
                else if (expiringMediaType == ActionType.SENT)
                    textRes = R.string.dms_inbox_raven_media_sent;
                else if (expiringMediaType == ActionType.OPENED)
                    textRes = R.string.dms_inbox_raven_media_opened;
                else if (expiringMediaType == ActionType.REPLAYED)
                    textRes = R.string.dms_inbox_raven_media_replayed;
                else if (expiringMediaType == ActionType.SENDING)
                    textRes = R.string.dms_inbox_raven_media_sending;
                else if (expiringMediaType == ActionType.BLOCKED)
                    textRes = R.string.dms_inbox_raven_media_blocked;
                else if (expiringMediaType == ActionType.SUGGESTED)
                    textRes = R.string.dms_inbox_raven_media_suggested;
                else if (expiringMediaType == ActionType.SCREENSHOT)
                    textRes = R.string.dms_inbox_raven_media_screenshot;
                else if (expiringMediaType == ActionType.CANNOT_DELIVER)
                    textRes = R.string.dms_inbox_raven_media_cant_deliver;
            }

            final RavenMediaViewMode ravenMediaViewMode = visualMedia.getViewType();
            if (ravenMediaViewMode == RavenMediaViewMode.PERMANENT || ravenMediaViewMode == RavenMediaViewMode.REPLAYABLE) {
                final MediaItemType mediaType = mediaModel.getMediaType();
                textRes = -1;
                binding.typeIcon.setVisibility(mediaType == MediaItemType.MEDIA_TYPE_VIDEO || mediaType == MediaItemType.MEDIA_TYPE_SLIDER
                                               ? View.VISIBLE
                                               : View.GONE);
                final Pair<Integer, Integer> widthHeight = NumberUtils.calculateWidthHeight(
                        mediaModel.getHeight(),
                        mediaModel.getWidth(),
                        maxHeight,
                        maxWidth
                );
                final ViewGroup.LayoutParams layoutParams = binding.ivMediaPreview.getLayoutParams();
                layoutParams.width = widthHeight.first != null ? widthHeight.first : 0;
                layoutParams.height = widthHeight.second != null ? widthHeight.second : 0;
                binding.ivMediaPreview.requestLayout();
                binding.ivMediaPreview.setImageURI(mediaModel.getThumbUrl());
            }
        }
        if (textRes != -1) {
            binding.tvMessage.setText(context.getText(textRes));
            binding.tvMessage.setVisibility(View.VISIBLE);
        }*/
    }

    private void setExpiryInfo(final DirectItemVisualMedia visualMedia) {
        final DirectItemMedia media = visualMedia.getMedia();
        final RavenMediaViewMode viewMode = visualMedia.getViewMode();
        if (viewMode != RavenMediaViewMode.PERMANENT) {
            final MediaItemType mediaType = media.getMediaType();
            final boolean expired = media.getPk() == null;
            final String info;
            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    if (expired) {
                        info = "Image has expired";
                        break;
                    }
                    info = "Image will expire when seen";
                    break;
                case MEDIA_TYPE_VIDEO:
                    if (expired) {
                        info = "Video has expired";
                        break;
                    }
                    info = "Video will expire when seen";
                    break;
                default:
                    if (expired) {
                        info = "Message has expired";
                        break;
                    }
                    info = "Message will expire when seen";
                    break;
            }
            binding.expiryInfo.setVisibility(View.VISIBLE);
            binding.expiryInfo.setText(info);
            return;
        }
        binding.expiryInfo.setVisibility(View.GONE);
    }

    private void setPreview(final DirectItemVisualMedia visualMedia,
                            final MessageDirection messageDirection) {
        final DirectItemMedia media = visualMedia.getMedia();
        final boolean expired = media.getPk() == null;
        if (expired) {
            binding.preview.setVisibility(View.GONE);
            binding.typeIcon.setVisibility(View.GONE);
            return;
        }
        final RoundingParams roundingParams = messageDirection == MessageDirection.INCOMING
                                              ? RoundingParams.fromCornersRadii(dmRadiusSmall, dmRadius, dmRadius, dmRadius)
                                              : RoundingParams.fromCornersRadii(dmRadius, dmRadiusSmall, dmRadius, dmRadius);
        binding.preview.setHierarchy(new GenericDraweeHierarchyBuilder(itemView.getResources())
                                             .setRoundingParams(roundingParams)
                                             .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                                             .build());
        final MediaItemType modelMediaType = media.getMediaType();
        binding.typeIcon.setVisibility(modelMediaType == MediaItemType.MEDIA_TYPE_VIDEO || modelMediaType == MediaItemType.MEDIA_TYPE_SLIDER
                                       ? View.VISIBLE
                                       : View.GONE);
        final Pair<Integer, Integer> widthHeight = NumberUtils.calculateWidthHeight(
                media.getOriginalHeight(),
                media.getOriginalWidth(),
                maxHeight,
                maxWidth
        );
        final ViewGroup.LayoutParams layoutParams = binding.preview.getLayoutParams();
        layoutParams.width = widthHeight.first != null ? widthHeight.first : 0;
        layoutParams.height = widthHeight.second != null ? widthHeight.second : 0;
        binding.preview.requestLayout();
        final ImageVersions2 imageVersions2 = media.getImageVersions2();
        if (imageVersions2 == null) return;
        final String thumbUrl = ResponseBodyUtils.getThumbUrl(imageVersions2);
        binding.preview.setImageURI(thumbUrl);
    }
}
