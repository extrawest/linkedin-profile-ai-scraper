# LinkedIn Profile Search Assistant

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)]()
[![Maintainer](https://img.shields.io/static/v1?label=Yevhen%20Ruban&message=Maintainer&color=red)](mailto:yevhen.ruban@extrawest.com)
[![Ask Me Anything !](https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg)]()
![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)
![GitHub release](https://img.shields.io/badge/release-v1.0.0-blue)

## Overview

The LinkedIn Profile Search Assistant is a tool designed to streamline the process of finding the most relevant LinkedIn profiles based on company information provided through Google search results. This service automates the task of identifying and selecting the appropriate LinkedIn account, saving time and ensuring accuracy.
How It Works

## Tech Stack

- **Java 21**
- **OpenAI**: Provides models for searching.
- **SpringBoot 3.2.4**: Backend framework for building fast and scalable applications.
- **SpringAI**: Orchestrates the entire process by creating a chain that integrates all functionalities.

### Input: The service receives search results from a Google search query that includes a company name.

### Processing: The tool analyzes the search results to identify LinkedIn profiles associated with the provided company name.

### Output: The service returns the most relevant LinkedIn profile link that matches the company and context from the search results.

## Usage

This service is ideal for users or organizations that need to quickly and accurately find LinkedIn profiles for specific companies without manually sifting through search results. It is particularly useful for recruitment, business development, or market research tasks.
Example

When provided with a Google search result for "OpenAI," the LinkedIn Profile Search Assistant will analyze the results and return the LinkedIn profile that best corresponds to OpenAI, ensuring that the profile is both accurate and relevant.

## Running On Local Machine (Linux):

1. Set up the following environment variables.
    - export OPENAI_API_KEY=your_api_key;
    - export TAVILY_API_KEY=your_api_key;
    - export GOOGLE_APPLICATION_CREDENTIALS=your_credentials;
2. Run the command: mvn exec:java -Dspring.profiles.active=local
3. Open the following link in your browser: http://localhost:8208/api/swagger-ui/index.html#/