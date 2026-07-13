# What is Retrieval-Augmented Generation (RAG)?
Retrieval-Augmented Generation (RAG) is a technique where an LLM does not rely solely on its internal training data. 
Instead, it retrieves relevant information from an external knowledge source and uses that content as context while
generating a response.

**In LangChain4j, RAG typically consists of three steps:**
* Splitting and embedding documents
* Storing embeddings in a vector store
* Retrieving relevant segments and injecting them into the prompt
* Also check out the RAG component tutorial here.

# Why RAG is Needed
LLMs are static after training and cannot know about your private data, 
internal documentation, or frequently changing content. RAG allows you to 
combine the reasoning capabilities of an LLM with up-to-date or domain-specific
knowledge without retraining the model.


