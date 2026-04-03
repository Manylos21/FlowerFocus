package com.example.flowerfocus.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Vue personnalisée qui dessine une fleur à l'aide de tracés vectoriels (Path).
 * La fleur est dessinée progressivement en fonction de la valeur de `progress` (0.0 à 1.0).
 *
 * Ordre de dessin :
 *  0.00 - 0.20 → La tige grandit
 *  0.20 - 0.40 → Les feuilles apparaissent
 *  0.40 - 1.00 → Les pétales se déploient un par un
 */
public class FlowerView extends View {

    private float progress = 0f;
    private String flowerType = "rose";

    private final Paint stemPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint leafPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint petalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path stemPath   = new Path();
    private final Path leafPath   = new Path();
    private final Path petalPath  = new Path();

    public FlowerView(Context context) {
        super(context); init();
    }

    public FlowerView(Context context, AttributeSet attrs) {
        super(context, attrs); init();
    }

    public FlowerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); init();
    }

    /**
     * Initialise les couleurs et les styles de dessin.
     */
    private void init() {
        stemPaint.setColor(Color.parseColor("#4CAF50")); // Vert pour la tige
        stemPaint.setStyle(Paint.Style.STROKE);
        stemPaint.setStrokeWidth(8f);
        stemPaint.setStrokeCap(Paint.Cap.ROUND);

        leafPaint.setColor(Color.parseColor("#66BB6A")); // Vert clair pour les feuilles
        leafPaint.setStyle(Paint.Style.FILL);

        petalPaint.setStyle(Paint.Style.FILL);

        centerPaint.setColor(Color.parseColor("#FFF176")); // Jaune pour le cœur
        centerPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Définit la progression de croissance (entre 0.0 et 1.0).
     */
    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate(); // Redessine la vue
    }

    /**
     * Définit le type de fleur et met à jour la couleur des pétales.
     */
    public void setFlowerType(String type) {
        this.flowerType = type;
        switch (type) {
            case "tulip":
                petalPaint.setColor(Color.parseColor("#E91E63"));
                break;
            case "daisy":
                petalPaint.setColor(Color.parseColor("#FFFFFF"));
                break;
            default: // rose
                petalPaint.setColor(Color.parseColor("#F06292"));
                break;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float flowerCenterY = cy - getHeight() * 0.1f;
        float stemBottomY = cy + getHeight() * 0.4f;

        // ── 1. Dessin de la tige (progression 0 → 0.2) ─────────────────────────
        if (progress > 0f) {
            float stemProgress = Math.min(progress / 0.2f, 1f);
            float stemTopY = stemBottomY - (stemBottomY - flowerCenterY) * stemProgress;

            stemPath.reset();
            stemPath.moveTo(cx, stemBottomY);
            stemPath.quadTo(cx + 15, (stemBottomY + stemTopY) / 2f, cx, stemTopY);
            canvas.drawPath(stemPath, stemPaint);
        }

        // ── 2. Dessin des feuilles (progression 0.2 → 0.4) ──────────────────────
        if (progress > 0.2f) {
            float leafProgress = Math.min((progress - 0.2f) / 0.2f, 1f);
            float leafY = stemBottomY - (stemBottomY - flowerCenterY) * 0.4f;
            float leafSize = 35f * leafProgress;

            // Feuille gauche
            leafPath.reset();
            leafPath.moveTo(cx, leafY);
            leafPath.quadTo(cx - leafSize * 1.5f, leafY - leafSize, cx - leafSize * 0.2f, leafY - leafSize * 0.3f);
            leafPath.close();
            canvas.drawPath(leafPath, leafPaint);

            // Feuille droite
            leafPath.reset();
            leafPath.moveTo(cx, leafY);
            leafPath.quadTo(cx + leafSize * 1.5f, leafY - leafSize, cx + leafSize * 0.2f, leafY - leafSize * 0.3f);
            leafPath.close();
            canvas.drawPath(leafPath, leafPaint);
        }

        // ── 3. Dessin des pétales (progression 0.4 → 1.0) ──────────────────────
        if (progress > 0.4f) {
            float petalProgress = (progress - 0.4f) / 0.6f;
            int totalPetals = 8;
            int petalsToShow = (int) (petalProgress * totalPetals);
            float partialPetal = (petalProgress * totalPetals) - petalsToShow;

            float petalLength = getWidth() * 0.22f;
            float petalWidth  = getWidth() * 0.09f;

            // Dessine les pétales complets
            for (int i = 0; i < petalsToShow; i++) {
                drawPetal(canvas, cx, flowerCenterY, i, totalPetals, petalLength, petalWidth, 1f);
            }
            // Dessine le pétale en cours de croissance
            if (petalsToShow < totalPetals) {
                drawPetal(canvas, cx, flowerCenterY, petalsToShow, totalPetals, petalLength, petalWidth, partialPetal);
            }

            // Cercle central (le cœur de la fleur)
            float centerRadius = 20f * Math.min(petalProgress * 2f, 1f);
            canvas.drawCircle(cx, flowerCenterY, centerRadius, centerPaint);
        }
    }

    /**
     * Méthode utilitaire pour dessiner un pétale individuel avec une rotation spécifique.
     */
    private void drawPetal(Canvas canvas, float cx, float cy,
                           int index, int total,
                           float length, float width, float scale) {
        float angle = (float) (2 * Math.PI * index / total) - (float) (Math.PI / 2);
        float dx = (float) Math.cos(angle);
        float dy = (float) Math.sin(angle);

        float tipX = cx + dx * length * scale;
        float tipY = cy + dy * length * scale;

        float perpX = -dy * width * scale;
        float perpY = dx * width * scale;

        petalPath.reset();
        petalPath.moveTo(cx, cy);
        petalPath.quadTo(cx + dx * length * 0.5f + perpX, cy + dy * length * 0.5f + perpY, tipX, tipY);
        petalPath.quadTo(cx + dx * length * 0.5f - perpX, cy + dy * length * 0.5f - perpY, cx, cy);
        petalPath.close();

        canvas.drawPath(petalPath, petalPaint);
    }
}
