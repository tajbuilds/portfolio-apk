package com.taj.portfolio.data

private fun String?.orEmptyTrimmed(): String = this?.trim().orEmpty()

private fun List<String>?.orCleanStrings(): List<String> = this.orEmpty().map { it.trim() }.filter { it.isNotBlank() }

fun HomeResponseDto.toDomain(): HomeResponse = HomeResponse(
    version = version.orEmptyTrimmed().ifBlank { "1.0" },
    generatedAt = generatedAt,
    profile = (profile ?: ProfileDto()).toDomain(),
    featuredWork = featuredWork.orEmpty().map { it.toDomain() },
    cta = (cta ?: CtaDto()).toDomain(),
)

fun WorkListResponseDto.toDomain(): WorkListResponse = WorkListResponse(
    version = version.orEmptyTrimmed().ifBlank { "1.0" },
    generatedAt = generatedAt,
    items = items.orEmpty().map { it.toDomain() },
)

fun WorkDetailResponseDto.toDomain(): WorkDetailResponse = WorkDetailResponse(
    version = version.orEmptyTrimmed().ifBlank { "1.0" },
    generatedAt = generatedAt,
    item = (item ?: WorkDetailDto()).toDomain(),
)

fun AboutResponseDto.toDomain(): AboutResponse = AboutResponse(
    version = version.orEmptyTrimmed().ifBlank { "1.0" },
    generatedAt = generatedAt,
    about = (about ?: AboutDto()).toDomain(),
)

fun ContactResponseDto.toDomain(): ContactResponse = ContactResponse(
    version = version.orEmptyTrimmed().ifBlank { "1.0" },
    generatedAt = generatedAt,
    contact = (contact ?: ContactDto()).toDomain(),
)

private fun ProfileDto.toDomain(): Profile = Profile(
    name = name.orEmptyTrimmed(),
    role = role.orEmptyTrimmed(),
    tagline = tagline.orEmptyTrimmed(),
    avatarUrl = avatarUrl.orEmptyTrimmed(),
    location = location.orEmptyTrimmed(),
)

private fun CtaDto.toDomain(): Cta = Cta(
    primary = (primary ?: CtaLinkDto()).toDomain(),
    secondary = (secondary ?: CtaLinkDto()).toDomain(),
)

private fun CtaLinkDto.toDomain(): CtaLink = CtaLink(
    label = label.orEmptyTrimmed(),
    path = path.orEmptyTrimmed(),
)

private fun WorkSummaryDto.toDomain(): WorkSummary = WorkSummary(
    slug = slug.orEmptyTrimmed(),
    title = title.orEmptyTrimmed(),
    summary = summary.orEmptyTrimmed(),
    tags = tags.orCleanStrings(),
    role = role.orEmptyTrimmed(),
    timeline = timeline.orEmptyTrimmed(),
    coverImageUrl = coverImageUrl.orEmptyTrimmed(),
    publishedAt = publishedAt.orEmptyTrimmed(),
    updatedAt = updatedAt.orEmptyTrimmed(),
)

private fun WorkDetailDto.toDomain(): WorkDetail = WorkDetail(
    slug = slug.orEmptyTrimmed(),
    title = title.orEmptyTrimmed(),
    summary = summary.orEmptyTrimmed(),
    tags = tags.orCleanStrings(),
    role = role.orEmptyTrimmed(),
    timeline = timeline.orEmptyTrimmed(),
    coverImageUrl = coverImageUrl.orEmptyTrimmed(),
    publishedAt = publishedAt.orEmptyTrimmed(),
    updatedAt = updatedAt.orEmptyTrimmed(),
    content = (content ?: WorkContentDto()).toDomain(),
    sections = sections?.toDomain(),
    links = links?.toDomain(),
)

private fun WorkContentDto.toDomain(): WorkContent = WorkContent(
    format = format.orEmptyTrimmed().ifBlank { "markdown" },
    body = body.orEmpty(),
)

private fun AboutDto.toDomain(): About = About(
    name = name.orEmptyTrimmed(),
    headline = headline.orEmptyTrimmed(),
    bio = bio.orEmptyTrimmed(),
    skills = skills.orCleanStrings(),
    focusAreas = focusAreas.orCleanStrings(),
    avatarUrl = avatarUrl?.trim()?.ifBlank { null },
    social = social.orEmpty().map { it.toDomain() },
)

private fun ContactDto.toDomain(): Contact = Contact(
    email = email.orEmptyTrimmed(),
    formPath = formPath.orEmptyTrimmed(),
    turnstileRequired = turnstileRequired ?: false,
    links = links.orEmpty().map { it.toDomain() },
)

private fun WorkSectionsDto.toDomain(): WorkSections = WorkSections(
    context = context?.trim(),
    constraints = constraints?.trim(),
    approach = approach?.trim(),
    outcome = outcome?.trim(),
    learnings = learnings?.trim(),
)

private fun WorkLinksDto.toDomain(): WorkLinks = WorkLinks(
    liveDemo = liveDemo?.trim(),
    repository = repository?.trim(),
)

private fun LinkItemDto.toDomain(): LinkItem = LinkItem(
    label = label.orEmptyTrimmed(),
    url = url.orEmptyTrimmed(),
)
