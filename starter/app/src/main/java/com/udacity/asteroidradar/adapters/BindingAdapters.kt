package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.WebServiceStatus

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("imageUrl")
fun bindImagePicasso(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Picasso.with(imgView.context).load(imgUrl)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(android.R.drawable.stat_notify_error )
            .fit()
            .into(imgView)
    }
}
//@BindingAdapter("imageUri")
//fun bindImageGlide(imgView: ImageView, imgUrl: String?) {
//    imgUrl?.let {
//        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
//        Glide.with(imgView.context)
//            .load(imgUri)
//            .into(imgView)
//    }
//}
@BindingAdapter("webServiceStatus")
fun bindWebServiceStatus(statusProgressBar: ProgressBar, status: WebServiceStatus) {
    when (status) {
        WebServiceStatus.LOADING -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        WebServiceStatus.ERROR -> {
            statusProgressBar.visibility = View.GONE
        }
        WebServiceStatus.DONE -> {
            statusProgressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("internetStatus")
fun bindWebServiceStatus(statusImageView: ImageView, status: WebServiceStatus) {
    when (status) {
        WebServiceStatus.LOADING -> {
            statusImageView.visibility = View.GONE
        }
        WebServiceStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(android.R.drawable.stat_notify_error)
        }
        WebServiceStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}