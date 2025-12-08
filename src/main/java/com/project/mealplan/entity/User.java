package com.project.mealplan.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.project.mealplan.common.enums.DietType;
import com.project.mealplan.common.enums.Gender;
import com.project.mealplan.common.enums.HealthCondition;
import com.project.mealplan.common.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(precision = 5, scale = 2)
  private BigDecimal height; // cm

  @Column(precision = 5, scale = 2)
  private BigDecimal weight; // kg

  private Integer age;

  @Column(length = 500)
  private String bio;

  @Column(name = "activity_level", length = 100)
  private String activityLevel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.INACTIVE;

  @Enumerated(EnumType.STRING)
  private HealthCondition healthCondition;

  @Enumerated(EnumType.STRING)
  private DietType dietType;

  private String profilePicUrl;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "favorites", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "recipe_id"))
  private Set<Recipe> favorites = new HashSet<>();

  @ToString.Exclude
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "createdBy")
  private Set<Recipe> recipes = new HashSet<>();
}
