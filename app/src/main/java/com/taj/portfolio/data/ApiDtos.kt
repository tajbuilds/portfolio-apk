package com.taj.portfolio.data

data class HomeResponseDto(
    val version: String? = null,
    val generatedAt: String? = null,
    val profile: ProfileDto? = null,
    val featuredWork: List<WorkSummaryDto>? = null,
    val cta: CtaDto? = null,
)

data class WorkListResponseDto(
    val version: String? = null,
    val generatedAt: String? = null,
    val items: List<WorkSummaryDto>? = null,
)

data class WorkDetailResponseDto(
    val version: String? = null,
    val generatedAt: String? = null,
    val item: WorkDetailDto? = null,
)

data class AboutResponseDto(
    val version: String? = null,
    val generatedAt: String? = null,
    val about: AboutDto? = null,
)

data class ContactResponseDto(
    val version: String? = null,
    val generatedAt: String? = null,
    val contact: ContactDto? = null,
)

data class ProfileDto(
    val name: String? = null,
    val role: String? = null,
    val tagline: String? = null,
    val avatarUrl: String? = null,
    val location: String? = null,
)

data class CtaDto(
    val primary: CtaLinkDto? = null,
    val secondary: CtaLinkDto? = null,
)

data class CtaLinkDto(
    val label: String? = null,
    val path: String? = null,
)

data class WorkSummaryDto(
    val slug: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val tags: List<String>? = null,
    val role: String? = null,
    val timeline: String? = null,
    val coverImageUrl: String? = null,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
)

data class WorkDetailDto(
    val slug: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val tags: List<String>? = null,
    val role: String? = null,
    val timeline: String? = null,
    val coverImageUrl: String? = null,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
    val content: WorkContentDto? = null,
    val sections: WorkSectionsDto? = null,
    val links: WorkLinksDto? = null,
)

data class WorkContentDto(
    val format: String? = null,
    val body: String? = null,
)

data class AboutDto(
    val name: String? = null,
    val headline: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val focusAreas: List<String>? = null,
    val avatarUrl: String? = null,
    val social: List<LinkItemDto>? = null,
)

data class ContactDto(
    val email: String? = null,
    val formPath: String? = null,
    val turnstileRequired: Boolean? = null,
    val links: List<LinkItemDto>? = null,
)

data class WorkSectionsDto(
    val context: String? = null,
    val constraints: String? = null,
    val approach: String? = null,
    val outcome: String? = null,
    val learnings: String? = null,
)

data class WorkLinksDto(
    val liveDemo: String? = null,
    val repository: String? = null,
)

data class LinkItemDto(
    val label: String? = null,
    val url: String? = null,
)

data class ContactSubmitRequestDto(
    val name: String,
    val email: String,
    val message: String,
    val company: String = "",
)

data class ContactSubmitResponseDto(
    val ok: Boolean? = null,
    val message: String? = null,
)
