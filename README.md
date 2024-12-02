# HN Top Links

A modern interface for browsing top stories from Hacker News, organized by different time periods. Similar to Reddit's "Top" feature, HN Top Links aggregates stories by daily, weekly, monthly and yearly intervals, letting you discover the most engaging content based on either upvotes or number of comments.

## Features

- Browse top stories by different time periods:
 - Today's best
 - Weekly digest
 - Monthly highlights
 - Yearly favorites
 - All-time best
- Sort stories by:
 - Most upvoted
 - Most commented
- Email subscription options:
 - Daily updates
 - Weekly digest
 - Monthly roundup
 - Yearly review
- Complete archive access back to 2006
- Mobile-friendly responsive design

## Tech Stack

- Backend
  - Java 17
  - Spring Boot
  - PostgreSQL
  - Flyway (migrations)
- Frontend
  - Thymeleaf
  - Modern responsive CSS
- Infrastructure
  - Docker
  - GitHub Actions

## Local Development Setup

1. Prerequisites:
   - Java 17
   - PostgreSQL 13+
   - Docker (optional)

2. Clone repository:
```bash
git clone https://github.com/eguller/hntoplinks.git
cd hntoplinks
```
3. Configure database:
```properties
# application-local.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hntoplinks
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. Run application:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

5. Access application at http://localhost:8066

## Testing
Run tests:
```bash
./gradlew test
```
Integration tests use TestContainers to spin up a PostgreSQL instance.
## Deployment
Using Docker:
```bash
docker-compose up -d
```

## Contributing
Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License
This project is licensed under MIT License - see the LICENSE file for details.

## Acknowledgments
Inspired by Reddit's "Top" feature and built with data from Hacker News. Thanks to Y Combinator for providing the HN API.
