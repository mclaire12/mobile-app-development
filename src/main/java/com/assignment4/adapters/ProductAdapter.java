package com.assignment4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agniment22.R;
import com.assignment4.models.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnItemClickListener listener;
    private OnItemActionListener actionListener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public interface OnItemActionListener {
        void onViewDetailClick(Product product);

        void onEditClick(Product product);

        void onDeleteClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void setOnItemActionListener(OnItemActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView categoryTextView;
        private TextView descriptionTextView;
        private MaterialButton btnViewDetail;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProduct);
            nameTextView = itemView.findViewById(R.id.textProductName);
            priceTextView = itemView.findViewById(R.id.textProductPrice);
            categoryTextView = itemView.findViewById(R.id.textProductCategory);
            descriptionTextView = itemView.findViewById(R.id.textProductDescription);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(products.get(position));
                }
            });

            btnViewDetail.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onViewDetailClick(products.get(position));
                }
            });

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onEditClick(products.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && actionListener != null) {
                    actionListener.onDeleteClick(products.get(position));
                }
            });
        }

        public void bind(Product product) {
            nameTextView.setText(product.getName());
            priceTextView.setText(String.format(Locale.getDefault(), "%,.0f RWF", product.getPrice()));

            // Load product image with Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.color.gray_light)
                        .error(R.color.gray_light)
                        .into(imageView);
            } else {
                // Show placeholder if no image
                imageView.setImageResource(android.R.color.transparent);
                imageView.setBackgroundResource(R.color.gray_light);
            }

            if (product.getCategory() != null) {
                categoryTextView.setText(product.getCategory().getName());
            } else {
                categoryTextView.setText("N/A");
            }

            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                descriptionTextView.setText(product.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }
        }
    }
}

