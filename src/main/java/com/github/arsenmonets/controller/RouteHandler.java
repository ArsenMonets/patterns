package com.github.arsenmonets.controller;

import io.javalin.http.Context;
import com.github.arsenmonets.models.User;
import com.github.arsenmonets.view.TemplateRenderer;
import com.github.arsenmonets.exception.AuthenticationException;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RouteHandler {

    public RouteHandler() {
    }

    public void handle(Context ctx, Consumer<Context> handler) {
        try {
            handler.accept(ctx);
        } catch (NumberFormatException e) {
            respondBadRequest(ctx, new IllegalArgumentException("Invalid number format", e));
        } catch (AuthenticationException e) {
            respondUnauthorized(ctx, e);
        } catch (IllegalAccessError e) {
            respondAccessDenied(ctx, e);
        } catch (IllegalArgumentException e) {
            respondBadRequest(ctx, e);
        } catch (Exception e) {
            respondServerError(ctx, e);
        }
    }

    public void handleAuthenticated(Context ctx, BiConsumer<Context, User> handler) {
        try {
            User user = getSessionUser(ctx);
            if (user == null) {
                ctx.redirect("/login");
                return;
            }
            handler.accept(ctx, user);
        } catch (NumberFormatException e) {
            respondBadRequest(ctx, new IllegalArgumentException("Invalid number format", e));
        } catch (AuthenticationException e) {
            respondUnauthorized(ctx, e);
        } catch (IllegalAccessError e) {
            respondAccessDenied(ctx, e);
        } catch (IllegalArgumentException e) {
            respondBadRequest(ctx, e);
        } catch (Exception e) {
            respondServerError(ctx, e);
        }
    }

    public void renderWithUser(Context ctx, String template, Map<String, Object> model) {
        User user = getSessionUser(ctx);
        model.put("user", user);
        ctx.html(TemplateRenderer.render(template, model));
    }

    public void renderTemplate(Context ctx, String template, Map<String, Object> model) {
        ctx.html(TemplateRenderer.render(template, model));
    }

    public User getSessionUser(Context ctx) {
        return ctx.sessionAttribute("user");
    }

    private void respondAccessDenied(Context ctx, IllegalAccessError e) {
        ctx.status(403);
        ctx.result("Access denied: " + e.getMessage());
    }

    private void respondUnauthorized(Context ctx, Exception e) {
        ctx.status(401);
        ctx.result("Unauthorized: " + e.getMessage());
    }

    private void respondBadRequest(Context ctx, Exception e) {
        ctx.status(400);
        ctx.result("Bad request: " + e.getMessage());
    }

    private void respondServerError(Context ctx, Exception e) {
        ctx.status(500);
        ctx.result("Internal server error: " + e.getMessage());
    }
}
