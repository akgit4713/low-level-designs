"""
Physics Problem: Point motion in xy-plane
x = at, y = at(1 − αt)

Solution visualization for trajectory, velocity, and acceleration.
General solution for angle θ between velocity and acceleration vectors.
"""

import numpy as np
import matplotlib.pyplot as plt

# Constants (adjustable)
a = 2.0        # positive constant
alpha = 0.5    # positive constant α
theta_deg = 45 # angle in degrees (can be changed)
theta = np.radians(theta_deg)  # convert to radians

# Time range
t = np.linspace(0, 2.5/alpha, 500)

# ==================== PART (a): Trajectory ====================
# Parametric equations
x = a * t
y = a * t * (1 - alpha * t)

# Direct trajectory equation: y(x) = x - (α/a)x²
x_direct = np.linspace(0, a/(alpha), 500)
y_direct = x_direct - (alpha/a) * x_direct**2

# ==================== PART (b): Velocity & Acceleration ====================
# Velocity components
v_x = a * np.ones_like(t)
v_y = a * (1 - 2 * alpha * t)

# Velocity magnitude: v = a√[1 + (1-2αt)²]
v_magnitude = a * np.sqrt(1 + (1 - 2*alpha*t)**2)

# Acceleration (constant)
# w_x = 0, w_y = -2aα
w_x = 0
w_y = -2 * a * alpha
w_magnitude = 2 * a * alpha

# ==================== PART (c): General time t₀ for angle θ ====================
# From the derivation:
# cos(θ) = -(1-2αt₀) / √[1 + (1-2αt₀)²]
# This gives: 1 - 2αt₀ = -cot(θ)
# Therefore: t₀ = (1 + cot(θ)) / (2α)

# Handle edge cases
if np.abs(np.sin(theta)) < 1e-10:
    # θ = 0 or π (parallel or antiparallel)
    if theta < np.pi/2:
        t_0 = float('inf')  # Never parallel
    else:
        t_0 = 1/(2*alpha)  # Perpendicular to acceleration direction
else:
    cot_theta = np.cos(theta) / np.sin(theta)
    t_0 = (1 + cot_theta) / (2 * alpha)

# Position at t₀
x_t0 = a * t_0
y_t0 = a * t_0 * (1 - alpha * t_0)

# Velocity at t₀
v_x_t0 = a
v_y_t0 = a * (1 - 2 * alpha * t_0)

# ==================== PLOTTING ====================
fig, axes = plt.subplots(2, 2, figsize=(14, 12))
fig.suptitle(f'Point Motion: x = at, y = at(1 - αt)\na = {a}, α = {alpha}', 
             fontsize=14, fontweight='bold')

# Plot (a): Trajectory y(x)
ax1 = axes[0, 0]
ax1.plot(x_direct, y_direct, 'b-', linewidth=2.5, label=r'$y = x - \frac{\alpha}{a}x^2$')
ax1.axhline(y=0, color='k', linestyle='-', linewidth=0.5)
ax1.axvline(x=0, color='k', linestyle='-', linewidth=0.5)

# Mark key points
ax1.plot(0, 0, 'go', markersize=10, label='Start (t=0)')
max_y_x = a / (2*alpha)  # x at maximum y
max_y = max_y_x - (alpha/a) * max_y_x**2
ax1.plot(max_y_x, max_y, 'r^', markersize=10, label=f'Max height (t={1/(2*alpha):.2f})')
if np.isfinite(t_0) and t_0 >= 0:
    ax1.plot(x_t0, y_t0, 'ms', markersize=10, label=f'At t₀={t_0:.2f}')
landing_x = a / alpha
ax1.plot(landing_x, 0, 'ko', markersize=10, label=f'Landing (t={1/alpha:.2f})')

ax1.set_xlabel('x', fontsize=12)
ax1.set_ylabel('y', fontsize=12)
ax1.set_title('(a) Trajectory y(x) - Parabola', fontsize=12)
ax1.legend(loc='upper right')
ax1.grid(True, alpha=0.3)
ax1.set_xlim(-0.5, landing_x + 1)
ax1.set_ylim(-0.5, max_y + 0.5)

# Plot (b1): Velocity magnitude vs time
ax2 = axes[0, 1]
ax2.plot(t, v_magnitude, 'r-', linewidth=2.5, label=r'$v = a\sqrt{1 + (1-2\alpha t)^2}$')
if np.isfinite(t_0) and t_0 >= 0:
    ax2.axvline(x=t_0, color='m', linestyle='--', linewidth=1.5, label=f't₀ = {t_0:.2f}')
ax2.axvline(x=1/(2*alpha), color='g', linestyle=':', linewidth=1.5, label=f'v_min at t={1/(2*alpha):.2f}')

# Mark minimum velocity (when 1-2αt = 0, i.e., t = 1/(2α))
t_min_v = 1 / (2*alpha)
v_min = a
ax2.plot(t_min_v, v_min, 'g^', markersize=10)

ax2.set_xlabel('Time t', fontsize=12)
ax2.set_ylabel('Velocity magnitude v', fontsize=12)
ax2.set_title('(b) Velocity magnitude vs Time', fontsize=12)
ax2.legend(loc='upper right')
ax2.grid(True, alpha=0.3)

# Plot (b2): Velocity components
ax3 = axes[1, 0]
ax3.plot(t, v_x, 'b-', linewidth=2, label=r'$v_x = a$ (constant)')
ax3.plot(t, v_y, 'r-', linewidth=2, label=r'$v_y = a(1 - 2\alpha t)$')
ax3.axhline(y=0, color='k', linestyle='-', linewidth=0.5)
ax3.axhline(y=-w_magnitude, color='purple', linestyle='--', linewidth=1.5, 
            label=f'w = 2aα = {w_magnitude:.1f} (constant)')
if np.isfinite(t_0) and t_0 >= 0:
    ax3.axvline(x=t_0, color='m', linestyle='--', alpha=0.7)

ax3.set_xlabel('Time t', fontsize=12)
ax3.set_ylabel('Velocity/Acceleration', fontsize=12)
ax3.set_title('(b) Velocity components & Acceleration', fontsize=12)
ax3.legend(loc='upper right')
ax3.grid(True, alpha=0.3)

# Plot (c): Angle between v and w
ax4 = axes[1, 1]

# Calculate angle between velocity and acceleration vectors
# cos(θ) = (v·w) / (|v||w|)
# v = (a, a(1-2αt)), w = (0, -2aα)
# v·w = -2a²α(1-2αt)
dot_product = -2 * a**2 * alpha * (1 - 2*alpha*t)
angle_cos = dot_product / (v_magnitude * w_magnitude)
angle = np.arccos(np.clip(angle_cos, -1, 1))  # in radians
angle_deg_plot = np.degrees(angle)

ax4.plot(t, angle_deg_plot, 'g-', linewidth=2.5, label='Angle θ between v and w')
ax4.axhline(y=theta_deg, color='r', linestyle='--', linewidth=2, label=f'θ = {theta_deg}°')
if np.isfinite(t_0) and t_0 >= 0:
    ax4.axvline(x=t_0, color='m', linestyle='--', linewidth=2, label=f't₀ = {t_0:.2f}')
    ax4.plot(t_0, theta_deg, 'ro', markersize=12)

ax4.set_xlabel('Time t', fontsize=12)
ax4.set_ylabel('Angle θ (degrees)', fontsize=12)
ax4.set_title(f'(c) Angle between velocity and acceleration vectors', fontsize=12)
ax4.legend(loc='upper right')
ax4.grid(True, alpha=0.3)
ax4.set_ylim(0, 180)

plt.tight_layout()
plt.savefig('/Users/anuragkumar/workspace/local-cp-chores/physics_trajectory.png', dpi=150, bbox_inches='tight')
plt.show()

# Print summary
print("="*70)
print("PHYSICS PROBLEM SOLUTION SUMMARY")
print("="*70)
print(f"\nGiven: x = at, y = at(1 - αt), with a = {a}, α = {alpha}")

print("\n" + "="*70)
print("(a) TRAJECTORY EQUATION y(x)")
print("="*70)
print(f"\n    From x = at  →  t = x/a")
print(f"    Substituting into y = at(1 - αt):")
print(f"    y = a·(x/a)·(1 - α·x/a) = x·(1 - αx/a)")
print(f"\n    ┌─────────────────────────────────┐")
print(f"    │  y(x) = x - (α/a)x²             │")
print(f"    │  y(x) = x - {alpha/a:.2f}x²  (numerical)   │")
print(f"    └─────────────────────────────────┘")
print(f"\n    This is a downward-opening PARABOLA (like projectile motion)")
print(f"    • Maximum height: y_max = a/(4α) = {max_y:.3f} at x = a/(2α) = {max_y_x:.3f}")
print(f"    • Range: x = a/α = {landing_x:.3f}")

print("\n" + "="*70)
print("(b) VELOCITY AND ACCELERATION")
print("="*70)
print(f"\n    Velocity components:")
print(f"      vₓ = dx/dt = a = {a}")
print(f"      vᵧ = dy/dt = d/dt[at - aαt²] = a - 2aαt = a(1 - 2αt)")
print(f"\n    ┌─────────────────────────────────────────────┐")
print(f"    │  v⃗ = (a, a(1-2αt))                          │")
print(f"    │  |v| = a√[1 + (1-2αt)²]                     │")
print(f"    └─────────────────────────────────────────────┘")
print(f"\n    • Minimum velocity: v_min = a = {a} at t = 1/(2α) = {t_min_v:.3f}")
print(f"\n    Acceleration components:")
print(f"      wₓ = dvₓ/dt = 0")
print(f"      wᵧ = dvᵧ/dt = -2aα = {w_y:.3f}")
print(f"\n    ┌─────────────────────────────────────────────┐")
print(f"    │  w⃗ = (0, -2aα)                              │")
print(f"    │  |w| = 2aα = {w_magnitude:.3f} (constant)              │")
print(f"    └─────────────────────────────────────────────┘")
print(f"\n    The acceleration is CONSTANT and points in the -y direction")

print("\n" + "="*70)
print(f"(c) TIME t₀ WHEN ANGLE BETWEEN v⃗ AND w⃗ IS θ = {theta_deg}°")
print("="*70)
print(f"\n    Using: cos(θ) = (v⃗ · w⃗) / (|v⃗| |w⃗|)")
print(f"\n    v⃗ · w⃗ = a·0 + a(1-2αt)·(-2aα) = -2a²α(1-2αt)")
print(f"\n    cos(θ) = -2a²α(1-2αt) / [a√(1+(1-2αt)²) · 2aα]")
print(f"           = -(1-2αt) / √[1+(1-2αt)²]")
print(f"\n    Let u = 1-2αt₀. Then: cos(θ) = -u/√(1+u²)")
print(f"    Solving: u² = cot²(θ)  →  u = -cot(θ)")
print(f"\n    Therefore: 1 - 2αt₀ = -cot(θ)")
print(f"\n    ┌─────────────────────────────────────────────┐")
print(f"    │  t₀ = (1 + cot θ) / (2α)                    │")
print(f"    │  t₀ = (tan θ + 1) / (2α tan θ)              │")
print(f"    └─────────────────────────────────────────────┘")

if np.isfinite(t_0) and t_0 >= 0:
    print(f"\n    For θ = {theta_deg}° = {theta:.4f} rad:")
    print(f"    cot({theta_deg}°) = {1/np.tan(theta):.4f}")
    print(f"\n    ┌─────────────────────────────────────────────┐")
    print(f"    │  t₀ = {t_0:.4f}                                │")
    print(f"    └─────────────────────────────────────────────┘")
    print(f"\n    At t₀ = {t_0:.4f}:")
    print(f"      Position: ({x_t0:.3f}, {y_t0:.3f})")
    print(f"      Velocity: v⃗ = ({v_x_t0:.3f}, {v_y_t0:.3f})")
    print(f"      |v| = {np.sqrt(v_x_t0**2 + v_y_t0**2):.3f}")

print("\n" + "="*70)
print("SPECIAL CASES:")
print("="*70)
print(f"\n    θ = 45° (π/4):  t₀ = (1+1)/(2α) = 1/α")
print(f"    θ = 90° (π/2):  t₀ = (1+0)/(2α) = 1/(2α)  [at max height]")
print(f"    θ = 135° (3π/4): t₀ = (1-1)/(2α) = 0  [at start]")
print("="*70)
