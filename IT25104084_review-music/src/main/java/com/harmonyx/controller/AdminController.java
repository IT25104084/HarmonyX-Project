package com.harmonyx.controller;

import com.harmonyx.config.AuthInterceptor;
import com.harmonyx.dto.SessionUser;
import com.harmonyx.dto.SongReviewForm;
import com.harmonyx.model.User;
import com.harmonyx.service.AdminService;
import com.harmonyx.service.AuthService;
import com.harmonyx.service.MusicService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ADMIN MODULE — all routes under /admin/**.
 * Protected by AuthInterceptor (ADMIN role only).
 * Covers: dashboard, user management, artist verification, music review, reports, audit.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final MusicService musicService;
    private final AuthService authService;

    public AdminController(AdminService adminService,
                           MusicService musicService,
                           AuthService authService) {
        this.adminService = adminService;
        this.musicService = musicService;
        this.authService = authService;
    }

    private User getAdminUser(HttpSession session) {
        SessionUser cu = AuthInterceptor.currentUser(session);
        return authService.getUser(cu.getUserId());
    }

    // ---- Dashboard ----

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("totalUsers", adminService.getTotalUserCount());
        model.addAttribute("totalSongs", adminService.getTotalSongCount());
        model.addAttribute("pendingReviews", adminService.getPendingReviewCount());
        model.addAttribute("pendingVerifications", adminService.getPendingVerificationCount());
        model.addAttribute("recentLogs", adminService.getRecentAuditLogs().stream().limit(10).toList());
        return "admin/dashboard";
    }

    // ---- User Management ----

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/suspend")
    public String suspendUser(@PathVariable Long id,
                              @RequestParam(defaultValue = "Policy violation") String reason,
                              HttpSession session, RedirectAttributes ra) {
        try {
            adminService.suspendUser(id, getAdminUser(session), reason);
            ra.addFlashAttribute("success", "User suspended.");
            ra.addFlashAttribute("successMessage", "User suspended.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reactivate")
    public String reactivateUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        try {
            adminService.reactivateUser(id, getAdminUser(session));
            ra.addFlashAttribute("success", "User reactivated.");
            ra.addFlashAttribute("successMessage", "User reactivated.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ---- Artist Verification ----

    @GetMapping("/artists")
    public String artists(Model model) {
        model.addAttribute("artists", adminService.getAllArtists());
        return "admin/artists";
    }

    @PostMapping("/artists/{id}/verify")
    public String verifyArtist(@PathVariable Long id, @RequestParam boolean approve,
                               HttpSession session, RedirectAttributes ra) {
        try {
            adminService.verifyArtist(id, approve, getAdminUser(session));
            String msg = approve ? "Artist verified." : "Artist verification rejected.";
            ra.addFlashAttribute("success", msg);
            ra.addFlashAttribute("successMessage", msg);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/artists";
    }

    // ---- Music Review ----

    @GetMapping({"/music", "/music-review"})
    public String musicReview(Model model) {
        model.addAttribute("pendingSongs", musicService.getPendingReviewSongs());
        return "admin/music-review";
    }

    @GetMapping({"/music/{id}", "/music-review/{id}"})
    public String musicReviewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("song", musicService.getSong(id));
        model.addAttribute("reviewForm", new SongReviewForm());
        return "admin/music-review-detail";
    }

    @PostMapping({"/music/{id}/review", "/music-review/{id}/review"})
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("reviewForm") SongReviewForm form,
                               BindingResult binding, HttpSession session,
                               Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("song", musicService.getSong(id));
            return "admin/music-review-detail";
        }
        try {
            musicService.reviewSong(id, form, getAdminUser(session));
            ra.addFlashAttribute("success", "Review submitted.");
            ra.addFlashAttribute("successMessage", "Review submitted.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/music";
    }

    // ---- Reports ----

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalUsers", adminService.getTotalUserCount());
        model.addAttribute("totalSongs", adminService.getTotalSongCount());
        model.addAttribute("totalStreams", adminService.getTotalStreamCount());
        model.addAttribute("usersByRole", adminService.getUsersByRole());
        model.addAttribute("songsByStatus", adminService.getSongsByStatus());
        model.addAttribute("songsByGenre", adminService.getSongsByGenre());
        model.addAttribute("topSongs", adminService.getTop10SongsByStreams());
        return "admin/reports";
    }

    // ---- Audit Log ----

    @GetMapping("/audit")
    public String audit(Model model) {
        model.addAttribute("logs", adminService.getRecentAuditLogs());
        return "admin/audit";
    }
}
