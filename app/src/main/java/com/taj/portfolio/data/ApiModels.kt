package com.taj.portfolio.data

data class HomeResponse(
    val version: String,
    val generatedAt: String,
    val profile: Profile,
    val featuredWork: List<WorkSummary>,
    val cta: Cta,
)

data class WorkListResponse(
    val version: String,
    val generatedAt: String,
    val items: List<WorkSummary>,
)

data class WorkDetailResponse(
    val version: String,
    val generatedAt: String,
    val item: WorkDetail,
)

data class AboutResponse(
    val version: String,
    val generatedAt: String,
    val about: About,
)

data class ContactResponse(
    val version: String,
    val generatedAt: String,
    val contact: Contact,
)

data class Profile(
    val name: String,
    val role: String,
    val tagline: String,
    val avatarUrl: String,
    val location: String,
)

data class Cta(
    val primary: CtaLink,
    val secondary: CtaLink,
)

data class CtaLink(
    val label: String,
    val path: String,
)

data class WorkSummary(
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

data class WorkDetail(
    val slug: String,
    val title: String,
    val summary: String,
    val tags: List<String>,
    val role: String,
    val timeline: String,
    val coverImageUrl: String,
    val publishedAt: String,
    val updatedAt: String,
    val content: WorkContent,
    val sections: WorkSections? = null,
    val links: WorkLinks? = null,
)

data class WorkContent(
    val format: String,
    val body: String,
)

data class About(
    val name: String,
    val headline: String,
    val bio: String,
    val skills: List<String>,
    val focusAreas: List<String>,
    val avatarUrl: String? = null,
    val social: List<LinkItem> = emptyList(),
)

data class Contact(
    val email: String,
    val formPath: String,
    val turnstileRequired: Boolean,
    val links: List<LinkItem> = emptyList(),
)

data class WorkSections(
    val context: String? = null,
    val constraints: String? = null,
    val approach: String? = null,
    val outcome: String? = null,
    val learnings: String? = null,
)

data class WorkLinks(
    val liveDemo: String? = null,
    val repository: String? = null,
)

data class LinkItem(
    val label: String,
    val url: String,
)
