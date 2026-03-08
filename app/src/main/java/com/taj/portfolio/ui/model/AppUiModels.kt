package com.taj.portfolio.ui.model

import com.taj.portfolio.data.About
import com.taj.portfolio.data.Contact
import com.taj.portfolio.data.Cta
import com.taj.portfolio.data.CtaLink
import com.taj.portfolio.data.LinkItem
import com.taj.portfolio.data.Profile
import com.taj.portfolio.data.WorkDetail
import com.taj.portfolio.data.WorkLinks
import com.taj.portfolio.data.WorkSections
import com.taj.portfolio.data.WorkSummary

data class ProfileUi(
    val name: String,
    val role: String,
    val tagline: String,
    val avatarUrl: String,
    val location: String,
)

data class CtaUi(
    val primary: CtaLinkUi,
    val secondary: CtaLinkUi,
)

data class CtaLinkUi(
    val label: String,
    val path: String,
)

data class WorkSummaryUi(
    val slug: String,
    val title: String,
    val summary: String,
    val tags: List<String>,
    val role: String,
    val timeline: String,
    val coverImageUrl: String,
    val publishedAt: String,
    val updatedAt: String,
)

data class WorkDetailUi(
    val slug: String,
    val title: String,
    val summary: String,
    val tags: List<String>,
    val role: String,
    val timeline: String,
    val coverImageUrl: String,
    val publishedAt: String,
    val updatedAt: String,
    val sections: WorkSectionsUi? = null,
    val links: WorkLinksUi? = null,
)

data class AboutUi(
    val name: String,
    val headline: String,
    val bio: String,
    val skills: List<String>,
    val focusAreas: List<String>,
    val avatarUrl: String? = null,
    val social: List<LinkItemUi> = emptyList(),
)

data class ContactUi(
    val email: String,
    val formPath: String,
    val turnstileRequired: Boolean,
    val links: List<LinkItemUi> = emptyList(),
)

data class WorkSectionsUi(
    val context: String? = null,
    val constraints: String? = null,
    val approach: String? = null,
    val outcome: String? = null,
    val learnings: String? = null,
)

data class WorkLinksUi(
    val liveDemo: String? = null,
    val repository: String? = null,
)

data class LinkItemUi(
    val label: String,
    val url: String,
)

fun Profile.toUi(): ProfileUi = ProfileUi(
    name = name,
    role = role,
    tagline = tagline,
    avatarUrl = avatarUrl,
    location = location,
)

fun Cta.toUi(): CtaUi = CtaUi(
    primary = primary.toUi(),
    secondary = secondary.toUi(),
)

fun CtaLink.toUi(): CtaLinkUi = CtaLinkUi(
    label = label,
    path = path,
)

fun WorkSummary.toUi(): WorkSummaryUi = WorkSummaryUi(
    slug = slug,
    title = title,
    summary = summary,
    tags = tags,
    role = role,
    timeline = timeline,
    coverImageUrl = coverImageUrl,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
)

fun WorkDetail.toUi(): WorkDetailUi = WorkDetailUi(
    slug = slug,
    title = title,
    summary = summary,
    tags = tags,
    role = role,
    timeline = timeline,
    coverImageUrl = coverImageUrl,
    publishedAt = publishedAt,
    updatedAt = updatedAt,
    sections = sections?.toUi(),
    links = links?.toUi(),
)

fun About.toUi(): AboutUi = AboutUi(
    name = name,
    headline = headline,
    bio = bio,
    skills = skills,
    focusAreas = focusAreas,
    avatarUrl = avatarUrl,
    social = social.map { it.toUi() },
)

fun Contact.toUi(): ContactUi = ContactUi(
    email = email,
    formPath = formPath,
    turnstileRequired = turnstileRequired,
    links = links.map { it.toUi() },
)

fun WorkSections.toUi(): WorkSectionsUi = WorkSectionsUi(
    context = context,
    constraints = constraints,
    approach = approach,
    outcome = outcome,
    learnings = learnings,
)

fun WorkLinks.toUi(): WorkLinksUi = WorkLinksUi(
    liveDemo = liveDemo,
    repository = repository,
)

fun LinkItem.toUi(): LinkItemUi = LinkItemUi(
    label = label,
    url = url,
)
